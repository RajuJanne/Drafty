<template>
  <div v-if="store.series" class="min-h-screen bg-slate-950 text-slate-100 p-4 font-sans">
    <header class="text-center mb-8">
      <h1 class="text-3xl font-black uppercase tracking-tst">Fearless Draft Tool</h1>
      <div
        class="flex items-center justify-center gap-12 bg-slate-900/40 py-4 px-8 rounded-full border border-slate-800"
      >
        <div class="text-right">
          <h2 class="text-2xl font-black text-blue-500">{{ store.series.teamBlueName }}</h2>
          <div class="flex justify-end gap-1 mt-1">
            <div
              v-for="i in Math.ceil(store.series.seriesLength / 2)"
              :key="i"
              class="w-3 h-1 rounded-full"
              :class="i <= store.series.blueScore ? 'bg-blue-500' : 'bg-slate-800'"
            ></div>
          </div>
        </div>
        <div class="text-left">
          <h2 class="text-2xl font-black text-red-500">{{ store.series.teamRedName }}</h2>
          <div class="flex justify-start gap-1 mt-1">
            <div
              v-for="i in Math.ceil(store.series.seriesLength / 2)"
              :key="i"
              class="w-3 h-1 rounded-full"
              :class="i <= store.series.redScore ? 'bg-red-500' : 'bg-slate-800'"
            ></div>
          </div>
        </div>
      </div>
    </header>

    <main class="grid grid-cols-12 gap-4 max-w-7xl mx-auto pb-24">
      <section class="col-span-2 space-y-4">
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
          class="h-24 bg-slate-900 border rounded overflow-hidden"
          :class="
            isActiveSlot('BLUE', i)
              ? 'border-blue-400 ring-2 ring-blue-500/50 scale-105 shadow-blue-glow'
              : 'border-slate-800'
          "
        >
          <img
            v-if="getPicks('BLUE')[i - 1]"
            :src="getImg(getPicks('BLUE')[i - 1])"
            class="h-full w-full object-cover"
          />
          <div
            v-else
            class="h-full flex items-center justify-center text-[10px] font-bold text-slate-700 uppercase"
          >
            Pick {{ i }}
          </div>
        </div>
      </section>

      <section class="col-span-8 bg-slate-900/50 p-4 rounded-xl border border-slate-800 shadow-2xl">
        <input
          v-model="search"
          placeholder="Search Champions..."
          class="bg-slate-800 rounded px-4 py-2 w-full mb-4 outline-none text-sm"
        />
        <div class="text-center mb-6">
          <span
            class="bg-blue-900/30 border border-blue-500/50 px-6 py-1 rounded-full text-blue-400 font-mono text-xs uppercase"
          >
            {{ store.getPhaseLabel }}
          </span>
        </div>
        <div
          class="grid grid-cols-4 sm:grid-cols-6 md:grid-cols-8 lg:grid-cols-10 gap-2 overflow-y-auto max-h-[55vh] custom-scrollbar pr-2"
        >
          <div
            v-for="champ in filteredChamps"
            :key="champ.id"
            @click="handleChampionClick(champ)"
            :class="[
              'relative cursor-pointer group rounded border-2 transition-all',
              isChampionDisabled(champ.id)
                ? 'grayscale opacity-30 pointer-events-none'
                : 'border-transparent hover:border-blue-500 hover:scale-110',
            ]"
          >
            <img :src="getImg(champ.id)" class="rounded w-full h-full object-cover" />
          </div>
        </div>
      </section>

      <section class="col-span-2 space-y-4">
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
          class="h-24 bg-slate-900 border rounded overflow-hidden"
          :class="
            isActiveSlot('RED', i)
              ? 'border-red-400 ring-2 ring-red-500/50 scale-105 shadow-red-glow'
              : 'border-slate-800'
          "
        >
          <img
            v-if="getPicks('RED')[i - 1]"
            :src="getImg(getPicks('RED')[i - 1])"
            class="h-full w-full object-cover"
          />
          <div
            v-else
            class="h-full flex items-center justify-center text-[10px] font-bold text-slate-700 uppercase"
          >
            Pick {{ i }}
          </div>
        </div>
      </section>
    </main>

    <footer
      class="fixed bottom-0 left-0 right-0 z-50 transition-all duration-500 ease-in-out bg-slate-950 border-t border-slate-800 shadow-[0_-10px_30px_rgba(0,0,0,0.5)]"
      :class="isGraveyardOpen ? 'h-[40vh]' : 'h-16'"
    >
      <div
        class="h-16 flex items-center justify-between px-8 bg-slate-900/50 border-b border-slate-800/50"
      >
        <div class="flex items-center gap-4">
          <button
            @click="toggleGraveyard"
            class="flex items-center gap-2 text-xs font-black tracking-widest text-slate-400 hover:text-white transition-colors"
          >
            <span
              :class="isGraveyardOpen ? 'rotate-180' : ''"
              class="transition-transform duration-300"
              >▲</span
            >
            CHAMPION GRAVEYARD
          </button>
        </div>

        <div class="flex gap-4">
          <div
            v-if="isGameComplete && !seriesWinner"
            class="flex gap-2 bg-slate-950 p-1 rounded-lg border border-slate-700"
          >
            <span class="text-[9px] self-center px-2 text-slate-500 font-bold uppercase"
              >Record Winner:</span
            >
            <button
              @click.stop="store.advanceGame('BLUE')"
              class="bg-blue-600/20 hover:bg-blue-600 text-blue-400 hover:text-white px-4 py-1.5 rounded text-[10px] font-black transition-all"
            >
              BLUE WIN
            </button>
            <button
              @click.stop="store.advanceGame('RED')"
              class="bg-red-600/20 hover:bg-red-600 text-red-400 hover:text-white px-4 py-1.5 rounded text-[10px] font-black transition-all"
            >
              RED WIN
            </button>
          </div>
        </div>
      </div>

      <div v-if="isGraveyardOpen" class="p-6 h-[calc(40vh-64px)] overflow-y-auto custom-scrollbar">
        <div class="grid grid-cols-2 gap-12 max-w-7xl mx-auto">
          <div class="space-y-6">
            <h3
              class="text-blue-500 font-black text-[10px] uppercase tracking-[0.2em] border-l-2 border-blue-500 pl-3"
            >
              Blue Team Burned
            </h3>
            <div
              v-for="(gamePicks, idx) in blueBurnedByGame"
              :key="'bg' + idx"
              class="bg-slate-900/20 p-4 rounded border border-slate-800/50"
            >
              <p class="text-[9px] text-slate-500 font-bold mb-3">GAME {{ idx + 1 }}</p>
              <div class="flex flex-wrap gap-4">
                <div v-for="id in gamePicks" :key="id" class="text-center group">
                  <div
                    class="w-14 h-14 grayscale opacity-60 border border-blue-900/30 rounded overflow-hidden mb-1"
                  >
                    <img :src="getImg(id)" class="w-full h-full object-cover" />
                  </div>
                  <p class="text-[9px] text-slate-400 font-medium truncate w-14">
                    {{ getChampName(id) }}
                  </p>
                </div>
              </div>
            </div>
          </div>
          <div class="space-y-6">
            <h3
              class="text-red-500 font-black text-[10px] uppercase tracking-[0.2em] border-r-2 border-red-500 pr-3 text-right"
            >
              Red Team Burned
            </h3>
            <div
              v-for="(gamePicks, idx) in redBurnedByGame"
              :key="'rg' + idx"
              class="bg-slate-900/20 p-4 rounded border border-slate-800/50"
            >
              <p class="text-[9px] text-slate-500 font-bold mb-3 text-right">GAME {{ idx + 1 }}</p>
              <div class="flex flex-wrap gap-4 justify-end">
                <div v-for="id in gamePicks" :key="id" class="text-center group">
                  <div
                    class="w-14 h-14 grayscale opacity-60 border border-red-900/30 rounded overflow-hidden mb-1"
                  >
                    <img :src="getImg(id)" class="w-full h-full object-cover" />
                  </div>
                  <p class="text-[9px] text-slate-400 font-medium truncate w-14">
                    {{ getChampName(id) }}
                  </p>
                </div>
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
import { useRoute } from "vue-router"
import { useDraftStore } from "../stores/draftStore"

const route = useRoute()
const store = useDraftStore()
const search = ref("")
const isGraveyardOpen = ref(false)

const toggleGraveyard = () => {
  isGraveyardOpen.value = !isGraveyardOpen.value
}

const getImg = (id) => `https://ddragon.leagueoflegends.com/cdn/15.24.1/img/champion/${id}.png`

const getChampName = (id) => {
  const champ = store.champions.find((c) => c.id === id)
  return champ ? champ.name : id
}

const filteredChamps = computed(() => {
  return store.champions.filter((c) => c.name.toLowerCase().includes(search.value.toLowerCase()))
})

const isChampionDisabled = (id) => {
  if (!store.series) return true
  const game = store.series.games[store.series.games.length - 1]
  const currentInGame = [...game.blueBans, ...game.redBans, ...game.bluePicks, ...game.redPicks]
  return currentInGame.includes(id) || store.isFearlessDisabled(id)
}

const getPicks = (side) => {
  if (!store.series) return []
  const game = store.series.games[store.series.games.length - 1]
  return side === "BLUE" ? game.bluePicks : game.redPicks
}

const getBans = (side) => {
  if (!store.series) return []
  const game = store.series.games[store.series.games.length - 1]
  return side === "BLUE" ? game.blueBans : game.redBans
}

const isActiveSlot = (side, slot) => {
  if (!store.currentTurn || store.currentTurn.type !== "PICK") return false
  return store.currentTurn.side === side && getPicks(side).length === slot - 1
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

const isGameComplete = computed(() => {
  if (!store.series) return false
  const game = store.series.games[store.series.games.length - 1]
  return game.bluePicks.length === 5 && game.redPicks.length === 5
})

const seriesWinner = computed(() => {
  if (!store.series) return null
  const winsNeeded = Math.floor(store.series.seriesLength / 2) + 1
  if (store.series.blueScore >= winsNeeded) return store.series.teamBlueName
  if (store.series.redScore >= winsNeeded) return store.series.teamRedName
  return null
})

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

const handleChampionClick = (champ) => {
  store.selectChampion(champ.id)
}

onMounted(async () => {
  await store.fetchChampions()
  const sessionId = route.params.id
  if (sessionId) await store.fetchSession(sessionId)
})
</script>

<style scoped>
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

.shadow-blue-glow {
  animation: pulse-blue 2s infinite;
}
.shadow-red-glow {
  animation: pulse-red 2s infinite;
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
</style>
