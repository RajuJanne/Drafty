<template>
  <div class="min-h-screen bg-slate-950 text-slate-100 font-sans">
    <!-- SETUP PHASE -->

    <div
      v-if="!store.isSetupComplete"
      class="fixed inset-0 z-[100] bg-slate-950 flex items-center justify-center p-6"
    >
      <div
        class="max-w-2xl w-full bg-slate-900 border border-slate-800 rounded-2xl p-12 shadow-2xl"
      >
        <h2
          class="text-4xl font-black text-center mb-12 tracking-tighter uppercase italic text-blue-500"
        >
          Series Configuration
        </h2>

        <div class="space-y-8">
          <div class="grid grid-cols-2 gap-8">
            <div class="space-y-2">
              <label class="text-[10px] font-bold text-blue-400 uppercase tracking-widest"
                >Blue Side</label
              >
              <input
                v-model="store.teamNames.BLUE"
                class="w-full bg-slate-950 border border-slate-800 p-4 rounded-xl focus:ring-2 ring-blue-500 outline-none text-xl font-bold"
              />
            </div>
            <div class="space-y-2">
              <label
                class="text-[10px] font-bold text-red-400 uppercase tracking-widest text-right block"
                >Red Side</label
              >
              <input
                v-model="store.teamNames.RED"
                class="w-full bg-slate-950 border border-slate-800 p-4 rounded-xl focus:ring-2 ring-red-500 outline-none text-xl font-bold text-right"
              />
            </div>
          </div>

          <div class="space-y-4">
            <label
              class="text-[10px] font-bold text-slate-500 uppercase tracking-widest text-center block"
              >Series Format</label
            >
            <div class="flex justify-center gap-4">
              <button
                v-for="len in [1, 3, 5]"
                :key="len"
                @click="store.seriesLength = len"
                :class="
                  store.seriesLength === len
                    ? 'bg-blue-600 border-blue-400'
                    : 'bg-slate-950 border-slate-800 text-slate-500'
                "
                class="px-8 py-3 rounded-xl border-2 font-black transition-all hover:scale-105"
              >
                BO{{ len }}
              </button>
            </div>
          </div>

          <button
            @click="store.beginDraft"
            class="w-full bg-gradient-to-r from-blue-600 to-blue-700 hover:from-blue-500 hover:to-blue-600 py-5 rounded-2xl font-black text-xl uppercase tracking-widest shadow-xl shadow-blue-900/20 transition-all active:scale-95"
          >
            Launch Draft
          </button>
        </div>
      </div>
    </div>

    <!-- DRAFT PHASE -->

    <div v-else :class="{ 'blur-sm pointer-events-none': !store.isSetupComplete }">
      <div class="min-h-screen bg-slate-950 text-slate-100 p-4 font-sans">
        <header class="text-center mb-8">
          <h1 class="text-3xl font-black uppercase tracking-tst">Fearless Draft Tool</h1>
          <div
            class="flex items-center justify-center gap-12 bg-slate-900/40 py-4 px-8 rounded-full border border-slate-800"
          >
            <div class="text-right">
              <h2 class="text-2xl font-black text-blue-500">{{ store.series.teamBlueName }}</h2>
              <div class="flex justify-end gap-1 mt-1">
                <div
                  v-for="i in Math.ceil(store.seriesLength / 2)"
                  :key="i"
                  class="w-3 h-1 rounded-full"
                  :class="i <= blueWins ? 'bg-blue-500' : 'bg-slate-800'"
                ></div>
              </div>
            </div>

            <button
              @click="store.swapTeams"
              class="bg-slate-800 p-2 rounded-full hover:bg-slate-700 transition-colors"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-6 w-6 text-slate-400"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4"
                />
              </svg>
            </button>

            <div class="text-left">
              <h2 class="text-2xl font-black text-red-500">{{ store.series.teamRedName }}</h2>
              <div class="flex justify-start gap-1 mt-1">
                <div
                  v-for="i in Math.ceil(store.seriesLength / 2)"
                  :key="i"
                  class="w-3 h-1 rounded-full"
                  :class="i <= redWins ? 'bg-red-500' : 'bg-slate-800'"
                ></div>
              </div>
            </div>
          </div>
        </header>

        <main class="grid grid-cols-12 gap-4 max-w-7xl mx-auto pb-24">
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
              <img
                v-else
                :src="getImg(getPicks('BLUE')[i - 1])"
                class="h-full w-full object-cover"
              />
            </div>
          </section>

          <section
            class="col-span-8 bg-slate-900/50 p-4 rounded-xl border border-slate-800 shadow-2xl"
          >
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
              <img
                v-else
                :src="getImg(getPicks('RED')[i - 1])"
                class="h-full w-full object-cover"
              />
            </div>
          </section>
        </main>

        <footer
          class="fixed bottom-0 left-0 right-0 z-50 transition-all duration-500 ease-in-out bg-slate-950 border-t border-slate-800 shadow-[0_-10px_30px_rgba(0,0,0,0.5)]"
          :class="isGraveyardOpen ? 'h-[40vh]' : 'h-16'"
          @click="toggleGraveyard"
        >
          <div
            class="h-16 flex items-center justify-between px-8 bg-slate-900/50 border-b border-slate-800/50"
          >
            <div class="flex items-center gap-4">
              <button
                @click.stop="toggleGraveyard"
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
                class="flex gap-2 bg-slate-900 p-1 rounded-lg border border-slate-700"
              >
                <span class="text-[9px] self-center px-2 text-slate-500 font-bold uppercase"
                  >Who won?</span
                >
                <button
                  @click.stop="store.advanceGame('BLUE')"
                  class="bg-blue-600/20 hover:bg-blue-600 text-blue-400 hover:text-white px-4 py-1.5 rounded text-[10px] font-black transition-all"
                >
                  BLUE
                </button>
                <button
                  @click.stop="store.advanceGame('RED')"
                  class="bg-red-600/20 hover:bg-red-600 text-red-400 hover:text-white px-4 py-1.5 rounded text-[10px] font-black transition-all"
                >
                  RED
                </button>
              </div>
            </div>
          </div>

          <div
            v-if="isGraveyardOpen"
            class="p-6 h-[calc(40vh-64px)] overflow-y-auto custom-scrollbar"
          >
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
                  <p class="text-[9px] text-slate-500 font-bold mb-3 text-right">
                    GAME {{ idx + 1 }}
                  </p>
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
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue"
import { useDraftStore } from "./stores/draftStore"

const store = useDraftStore()
const search = ref("")
const isGraveyardOpen = ref(false)
const loading = ref(true)

const toggleGraveyard = () => {
  isGraveyardOpen.value = !isGraveyardOpen.value
}

const blueWins = computed(() => store.series?.blueScore || 0)
const redWins = computed(() => store.series?.redScore || 0)

const seriesWinner = computed(() => {
  if (!store.series) return null
  const winsNeeded = Math.floor(store.series.seriesLength / 2) + 1

  if (blueWins.value >= winsNeeded) return store.series.teamBlueName
  if (redWins.value >= winsNeeded) return store.series.teamRedName
  return null
})

const filteredChamps = computed(() => {
  if (!store.champions) return []

  return store.champions.filter((c) => {
    const isDisabled =
      typeof store.isFearlessDisabled === "function" ? store.isFearlessDisabled(c.id) : false

    if (isDisabled) return false

    return c.name.toLowerCase().includes(search.value.toLowerCase())
  })
})

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

const getChampName = (id) => {
  const champ = store.champions.find((c) => c.id === id)
  return champ ? champ.name : id
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
  await store.advanceGame()
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
