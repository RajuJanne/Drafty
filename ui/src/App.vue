<template>
  <div class="min-h-screen bg-slate-950 text-slate-100 p-4 font-sans">
    <header class="text-center mb-8">
      <h1 class="text-3xl font-black uppercase tracking-tst">Fearless Draft Tool</h1>
      <div class="flex justify-center items-center gap-8 mt-2">
        <span class="text-blue-500 font-bold text-xl">{{
          store.series?.teamBlueName || "BLUE"
        }}</span>
        <span class="text-slate-500 italic">VS</span>
        <span class="text-red-500 font-bold text-xl">{{ store.series?.teamRedName || "RED" }}</span>
      </div>
    </header>

    <main class="grid grid-cols-12 gap-4 max-w-7xl mx-auto">
      <section class="col-span-2 space-y-4 p-2 rounded-lg transition-all duration-500">
        <h2 class="text-blue-400 font-bold border-b border-blue-900 pb-1 text-sm uppercase">
          Blue Side
        </h2>

        <div class="flex gap-1 mb-2">
          <div
            v-for="i in 5"
            :key="'bb' + i"
            class="w-8 h-8 bg-slate-900 border rounded overflow-hidden transition-all"
            :class="getBanSlotClass('BLUE', i)"
          >
            <img
              v-if="getBans('BLUE')[i - 1]"
              :src="getImg(getBans('BLUE')[i - 1])"
              class="w-full h-full object-cover grayscale"
            />
          </div>
        </div>

        <div
          v-for="i in 5"
          :key="'bp' + i"
          class="h-24 bg-slate-900 border rounded flex items-center justify-center transition-all duration-300 overflow-hidden"
          :class="[
            isActiveSlot('BLUE', i)
              ? 'border-blue-400 ring-2 ring-blue-500/50 shadow-blue-glow scale-105'
              : 'border-slate-800',
          ]"
        >
          <span
            v-if="!getPicks('BLUE')[i - 1]"
            class="text-slate-700 text-[10px] font-bold uppercase"
            >Pick {{ i }}</span
          >
          <img v-else :src="getImg(getPicks('BLUE')[i - 1])" class="h-full w-full object-cover" />
        </div>
      </section>

      <section class="col-span-8 bg-slate-900/50 p-4 rounded-xl border border-slate-800 shadow-2xl">
        <div class="flex mb-4 gap-4">
          <input
            v-model="search"
            placeholder="Search Champions..."
            class="bg-slate-800 border-none rounded px-4 py-2 w-full focus:ring-2 focus:ring-blue-500 text-sm outline-none"
          />
        </div>

        <div class="text-center mb-6">
          <span
            class="bg-blue-900/30 border border-blue-500/50 px-6 py-1 rounded-full text-blue-400 font-mono text-xs uppercase tracking-tighter"
          >
            {{ store.getPhaseLabel || "Waiting..." }}
          </span>
        </div>

        <div
          v-if="!loading"
          class="grid grid-cols-4 sm:grid-cols-6 md:grid-cols-8 lg:grid-cols-10 gap-2 overflow-y-auto max-h-[60vh] custom-scrollbar pr-2"
        >
          <div
            v-for="champ in filteredChamps"
            :key="champ.id"
            @click="handleChampionClick(champ)"
            :class="[
              'relative cursor-pointer group rounded border-2 transition-all',
              isBannedInCurrentGame(champ.id) || isPickedInCurrentGame(champ.id)
                ? 'grayscale opacity-30 border-slate-900 pointer-events-none'
                : 'border-transparent hover:border-blue-500 hover:scale-110',
            ]"
          >
            <img :src="getImg(champ.imageFull)" class="rounded w-full h-full object-cover" />

            <div
              v-if="isBannedInCurrentGame(champ.id)"
              class="absolute inset-0 flex items-center justify-center pointer-events-none"
            >
              <span
                class="bg-red-600/90 text-[8px] text-white px-1 font-black rounded italic rotate-12 uppercase shadow-lg"
                >Banned</span
              >
            </div>

            <div
              class="absolute bottom-0 left-0 right-0 bg-black/80 opacity-0 group-hover:opacity-100 transition-opacity"
            >
              <p class="text-[8px] text-center py-0.5 truncate">{{ champ.name }}</p>
            </div>
          </div>
        </div>
      </section>

      <section class="col-span-2 space-y-4 p-2 rounded-lg transition-all duration-500">
        <h2
          class="text-red-400 font-bold border-b border-red-900 pb-1 text-right text-sm uppercase"
        >
          Red Side
        </h2>

        <div class="flex gap-1 mb-2 justify-end">
          <div
            v-for="i in 5"
            :key="'rb' + i"
            class="w-8 h-8 bg-slate-900 border rounded overflow-hidden transition-all"
            :class="getBanSlotClass('RED', i)"
          >
            <img
              v-if="getBans('RED')[i - 1]"
              :src="getImg(getBans('RED')[i - 1])"
              class="w-full h-full object-cover grayscale"
            />
          </div>
        </div>

        <div
          v-for="i in 5"
          :key="'rp' + i"
          class="h-24 bg-slate-900 border rounded flex items-center justify-center transition-all duration-300 overflow-hidden"
          :class="[
            isActiveSlot('RED', i)
              ? 'border-red-400 ring-2 ring-red-500/50 shadow-red-glow scale-105'
              : 'border-slate-800',
          ]"
        >
          <span
            v-if="!getPicks('RED')[i - 1]"
            class="text-slate-700 text-[10px] font-bold uppercase"
            >Pick {{ i }}</span
          >
          <img v-else :src="getImg(getPicks('RED')[i - 1])" class="h-full w-full object-cover" />
        </div>
      </section>
    </main>

    <footer class="mt-8 max-w-7xl mx-auto border-t border-slate-900 pt-4">
      <div class="flex justify-between items-center mb-4">
        <div>
          <h3 class="text-xs font-bold text-slate-500 uppercase tracking-widest">
            Fearless Blocked
          </h3>
          <p class="text-[10px] text-slate-600">Champions used in previous games are removed</p>
        </div>
        <div class="flex gap-4">
          <button
            v-if="isGameComplete"
            @click="advanceToNextGame"
            class="bg-blue-600 hover:bg-blue-500 px-6 py-2 rounded text-xs font-bold transition-all shadow-lg shadow-blue-900/20"
          >
            ADVANCE TO NEXT GAME
          </button>
          <button
            @click="store.resetSeries"
            class="px-4 py-2 bg-slate-800 hover:bg-red-900/50 rounded text-xs font-bold transition-colors border border-slate-700"
          >
            RESET SERIES
          </button>
        </div>
      </div>

      <div class="grid grid-cols-2 gap-16">
        <div class="space-y-6">
          <h3
            class="text-blue-500 font-black text-xs uppercase tracking-[0.2em] border-l-2 border-blue-500 pl-3"
          >
            Blue Team Burned
          </h3>
          <div
            v-for="(gamePicks, index) in blueBurnedByGame"
            :key="'blue-g' + index"
            class="bg-slate-900/30 p-3 rounded-lg border border-slate-800/50"
          >
            <p class="text-[10px] text-slate-500 font-bold mb-2 uppercase">Game {{ index + 1 }}</p>
            <div class="flex gap-2">
              <div
                v-for="id in gamePicks"
                :key="id"
                class="w-12 h-12 grayscale opacity-40 border border-blue-900/30 rounded overflow-hidden"
              >
                <img :src="getImg(id)" class="w-full h-full object-cover" />
              </div>
            </div>
          </div>
        </div>
        <div class="space-y-6">
          <h3
            class="text-red-500 font-black text-xs uppercase tracking-[0.2em] border-r-2 border-red-500 pr-3 text-right"
          >
            Red Team Burned
          </h3>
          <div
            v-for="(gamePicks, index) in redBurnedByGame"
            :key="'red-g' + index"
            class="bg-slate-900/30 p-3 rounded-lg border border-slate-800/50"
          >
            <p class="text-[10px] text-slate-500 font-bold mb-2 uppercase text-right">
              Game {{ index + 1 }}
            </p>
            <div class="flex gap-2 justify-end">
              <div
                v-for="id in gamePicks"
                :key="id"
                class="w-12 h-12 grayscale opacity-40 border border-red-900/30 rounded overflow-hidden"
              >
                <img :src="getImg(id)" class="w-full h-full object-cover" />
              </div>
            </div>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue"
import { useDraftStore } from "./stores/draftStore"

const store = useDraftStore()
const search = ref("")

const loading = ref(true)

const filteredChamps = computed(() => {
  if (!store.champions) return [];

  return store.champions.filter((c) => {
    const isDisabled = typeof store.isFearlessDisabled === 'function' 
      ? store.isFearlessDisabled(c.id) 
      : false;

    if (isDisabled) return false;

    return c.name.toLowerCase().includes(search.value.toLowerCase());
  });
});

const isBannedInCurrentGame = (championId) => {
  if (!store.series) return false
  const game = store.series.games[store.series.games.length - 1]
  return game.blueBans.includes(championId) || game.redBans.includes(championId)
}

const isPickedInCurrentGame = (championId) => {
  if (!store.series) return false
  const game = store.series.games[store.series.games.length - 1]
  return game.bluePicks.includes(championId) || game.redPicks.includes(championId)
}

const isActiveSlot = (side, slotNumber) => {
  if (!store.currentTurn || store.currentTurn.type !== "PICK") return false
  if (store.currentTurn.side !== side) return false
  const currentPicks = getPicks(side)
  return currentPicks.length === slotNumber - 1
}

const getBanSlotClass = (side, slotNumber) => {
  if (!store.currentTurn || store.currentTurn.type !== "BAN") return "border-slate-800"
  if (store.currentTurn.side !== side) return "border-slate-800"
  const currentBans = getBans(side)
  if (currentBans.length === slotNumber - 1) {
    return side === "BLUE"
      ? "border-blue-400 ring-1 ring-blue-400"
      : "border-red-400 ring-1 ring-red-400"
  }
  return "border-slate-800"
}

const handleChampionClick = (champ) => {
  if (
    store.isFearlessDisabled(champ.id) ||
    isBannedInCurrentGame(champ.id) ||
    isPickedInCurrentGame(champ.id)
  )
    return
  store.selectChampion(champ.id)
}

const getBans = (team) => {
  if (!store.series) return []
  const current = store.series.games[store.series.games.length - 1]
  return team === "BLUE" ? current.blueBans : current.redBans
}

const getPicks = (team) => {
  if (!store.series) return []
  const current = store.series.games[store.series.games.length - 1]
  return team === "BLUE" ? current.bluePicks : current.redPicks
}

const getImg = (input) => {
  if (!input) return ""
  const filename = input.endsWith(".png") ? input : `${input}.png`
  return `https://ddragon.leagueoflegends.com/cdn/15.24.1/img/champion/${filename}`
}

const isGameComplete = computed(() => {
  if (!store.series) return false
  const game = store.series.games[store.series.games.length - 1]
  return game.bluePicks.length === 5 && game.redPicks.length === 5
})

const advanceToNextGame = async () => {
  try {
    const res = await fetch(`http://localhost:8080/api/draft/${store.sessionId}/next-game`, {
      method: "POST",
    })
    store.series = await res.json()
  } catch (e) {
    console.error("Failed to advance game", e)
  }
}

const chunkArray = (array, size) => {
  if (!array) return []
  const result = []
  for (let i = 0; i < array.length; i += size) {
    result.push(array.slice(i, i + size))
  }
  return result
}

const blueBurnedByGame = computed(() => chunkArray(store.series?.blueBurnedPicks, 5))
const redBurnedByGame = computed(() => chunkArray(store.series?.redBurnedPicks, 5))

onMounted(async () => {
  await store.fetchChampions()
  await store.startNewSeries("T1", "GEN.G")
  loading.value = false
})
</script>

<style>
.custom-scrollbar::-webkit-scrollbar {
  width: 5px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #1e293b;
  border-radius: 10px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #334155;
}

@keyframes pulse-blue {
  0%,
  100% {
    box-shadow: 0 0 10px rgba(59, 130, 246, 0.3);
  }
  50% {
    box-shadow: 0 0 20px rgba(59, 130, 246, 0.6);
  }
}

@keyframes pulse-red {
  0%,
  100% {
    box-shadow: 0 0 10px rgba(239, 68, 68, 0.3);
  }
  50% {
    box-shadow: 0 0 20px rgba(239, 68, 68, 0.6);
  }
}

.shadow-blue-glow {
  animation: pulse-blue 2s infinite;
}
.shadow-red-glow {
  animation: pulse-red 2s infinite;
}
</style>
