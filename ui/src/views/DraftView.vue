<template>
  <div v-if="store.series" class="min-h-screen bg-slate-950 text-slate-100 p-4 font-sans">
    <header class="text-center mb-8">
      <h1 class="text-3xl font-black uppercase tracking-tst">Fearless Draft Tool</h1>
      <div class="flex items-center justify-center gap-12 bg-slate-900/40 py-4 px-8 rounded-full border border-slate-800">
        <div class="text-right">
          <h2 class="text-2xl font-black text-blue-500">{{ store.series.teamBlueName }}</h2>
          <div class="flex justify-end gap-1 mt-1">
            <div v-for="i in Math.ceil(store.series.seriesLength / 2)" :key="i"
              class="w-3 h-1 rounded-full" :class="i <= store.series.blueScore ? 'bg-blue-500' : 'bg-slate-800'"></div>
          </div>
        </div>
        <div class="text-left">
          <h2 class="text-2xl font-black text-red-500">{{ store.series.teamRedName }}</h2>
          <div class="flex justify-start gap-1 mt-1">
            <div v-for="i in Math.ceil(store.series.seriesLength / 2)" :key="i"
              class="w-3 h-1 rounded-full" :class="i <= store.series.redScore ? 'bg-red-500' : 'bg-slate-800'"></div>
          </div>
        </div>
      </div>
    </header>

    <main class="grid grid-cols-12 gap-4 max-w-7xl mx-auto pb-24">
      <section class="col-span-2 space-y-4">
        <div v-for="i in 5" :key="'bp'+i" class="h-24 bg-slate-900 border rounded overflow-hidden"
          :class="isActiveSlot('BLUE', i) ? 'border-blue-400 ring-2 ring-blue-500/50 scale-105 shadow-blue-glow' : 'border-slate-800'">
          <img v-if="getPicks('BLUE')[i-1]" :src="getImg(getPicks('BLUE')[i-1])" class="h-full w-full object-cover" />
          <div v-else class="h-full flex items-center justify-center text-[10px] font-bold text-slate-700 uppercase">Pick {{i}}</div>
        </div>
      </section>

      <section class="col-span-8 bg-slate-900/50 p-4 rounded-xl border border-slate-800 shadow-2xl">
        <input v-model="search" placeholder="Search Champions..." class="bg-slate-800 rounded px-4 py-2 w-full mb-4 outline-none text-sm" />
        <div class="text-center mb-6">
          <span class="bg-blue-900/30 border border-blue-500/50 px-6 py-1 rounded-full text-blue-400 font-mono text-xs uppercase">
            {{ store.getPhaseLabel }}
          </span>
        </div>
        <div class="grid grid-cols-4 sm:grid-cols-6 md:grid-cols-8 lg:grid-cols-10 gap-2 overflow-y-auto max-h-[55vh] custom-scrollbar pr-2">
          <div v-for="champ in filteredChamps" :key="champ.id" @click="handleChampionClick(champ)"
            :class="['relative cursor-pointer group rounded border-2 transition-all', 
              isChampionDisabled(champ.id) ? 'grayscale opacity-30 pointer-events-none' : 'border-transparent hover:border-blue-500 hover:scale-110']">
            <img :src="getImg(champ.id)" class="rounded w-full h-full object-cover" />
          </div>
        </div>
      </section>

      <section class="col-span-2 space-y-4">
        <div v-for="i in 5" :key="'rp'+i" class="h-24 bg-slate-900 border rounded overflow-hidden"
          :class="isActiveSlot('RED', i) ? 'border-red-400 ring-2 ring-red-500/50 scale-105 shadow-red-glow' : 'border-slate-800'">
          <img v-if="getPicks('RED')[i-1]" :src="getImg(getPicks('RED')[i-1])" class="h-full w-full object-cover" />
          <div v-else class="h-full flex items-center justify-center text-[10px] font-bold text-slate-700 uppercase">Pick {{i}}</div>
        </div>
      </section>
    </main>

    <footer v-if="isGameComplete" class="fixed bottom-8 left-1/2 -translate-x-1/2 z-50 bg-slate-900 border border-slate-700 p-4 rounded-2xl shadow-2xl flex items-center gap-6">
       <span class="text-sm font-bold uppercase tracking-widest text-slate-400">Record Winner:</span>
       <button @click="store.advanceGame('BLUE')" class="bg-blue-600 px-6 py-2 rounded-lg font-black hover:bg-blue-500 transition-colors">BLUE WIN</button>
       <button @click="store.advanceGame('RED')" class="bg-red-600 px-6 py-2 rounded-lg font-black hover:bg-red-500 transition-colors">RED WIN</button>
    </footer>
  </div>
  <div v-else class="flex items-center justify-center h-screen">
    <div class="animate-pulse text-blue-500 font-black tracking-widest uppercase">Syncing Draft State...</div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { useDraftStore } from '../stores/draftStore';

const route = useRoute();
const store = useDraftStore();
const search = ref("");

const getImg = (id) => `https://ddragon.leagueoflegends.com/cdn/15.24.1/img/champion/${id}.png`;

const filteredChamps = computed(() => {
  return store.champions.filter(c => c.name.toLowerCase().includes(search.value.toLowerCase()));
});

const isChampionDisabled = (id) => {
  if (!store.series) return true;
  const game = store.series.games[store.series.games.length - 1];
  const currentInGame = [...game.blueBans, ...game.redBans, ...game.bluePicks, ...game.redPicks];
  return currentInGame.includes(id) || store.isFearlessDisabled(id);
};

const getPicks = (side) => {
  if (!store.series) return [];
  const game = store.series.games[store.series.games.length - 1];
  return side === 'BLUE' ? game.bluePicks : game.redPicks;
};

const isActiveSlot = (side, slot) => {
  if (!store.currentTurn || store.currentTurn.type !== 'PICK') return false;
  return store.currentTurn.side === side && getPicks(side).length === slot - 1;
};

const isGameComplete = computed(() => {
  if (!store.series) return false;
  const game = store.series.games[store.series.games.length - 1];
  return game.bluePicks.length === 5 && game.redPicks.length === 5;
});

const handleChampionClick = (champ) => {
  store.selectChampion(champ.id);
};

onMounted(async () => {
  await store.fetchChampions();
  if (route.params.id) {
    await store.fetchSession(route.params.id);
  }
});
</script>

<style scoped>
.shadow-blue-glow { animation: pulse-blue 2s infinite; }
.shadow-red-glow { animation: pulse-red 2s infinite; }
@keyframes pulse-blue { 0%, 100% { box-shadow: 0 0 10px rgba(59, 130, 246, 0.3); } 50% { box-shadow: 0 0 20px rgba(59, 130, 246, 0.6); } }
@keyframes pulse-red { 0%, 100% { box-shadow: 0 0 10px rgba(239, 68, 68, 0.3); } 50% { box-shadow: 0 0 20px rgba(239, 68, 68, 0.6); } }
</style>