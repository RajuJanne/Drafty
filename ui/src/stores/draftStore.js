import { defineStore } from "pinia"

export const useDraftStore = defineStore("draft", {
  state: () => ({
    sessionId: null,
    series: null,
    champions: [],
    loading: false,
    turnOrder: [
      // PHASE 1: 3 Bans each
      { side: "BLUE", type: "BAN" },
      { side: "RED", type: "BAN" },
      { side: "BLUE", type: "BAN" },
      { side: "RED", type: "BAN" },
      { side: "BLUE", type: "BAN" },
      { side: "RED", type: "BAN" },

      // PHASE 1: 3 Picks each (B-R-R-B-B-R)
      { side: "BLUE", type: "PICK" },
      { side: "RED", type: "PICK" },
      { side: "RED", type: "PICK" },
      { side: "BLUE", type: "PICK" },
      { side: "BLUE", type: "PICK" },
      { side: "RED", type: "PICK" },

      // PHASE 2: 2 Bans each (R-B-R-B)
      { side: "RED", type: "BAN" },
      { side: "BLUE", type: "BAN" },
      { side: "RED", type: "BAN" },
      { side: "BLUE", type: "BAN" },

      // PHASE 2: 2 Picks each (R-B-B-R)
      { side: "RED", type: "PICK" },
      { side: "BLUE", type: "PICK" },
      { side: "BLUE", type: "PICK" },
      { side: "RED", type: "PICK" },
    ],
  }),

  actions: {
    async fetchChampions() {
      this.loading = true
      try {
        const response = await fetch("http://localhost:8080/api/champions")
        if (!response.ok) throw new Error("Failed to fetch champions")
        this.champions = await response.json()
      } catch (error) {
        console.error("Backend error:", error)
      } finally {
        this.loading = false
      }
    },

    async startNewSeries(blueName, redName) {
      try {
        const response = await fetch(
          `http://localhost:8080/api/draft/start?blueName=${blueName}&redName=${redName}`,
          {
            method: "POST",
          },
        )
        const data = await response.json()
        this.series = data
        this.sessionId = data.id
      } catch (error) {
        console.error("Start series error:", error)
      }
    },

    async resetSeries() {
      const bName = this.series?.teamBlueName || "Blue Team"
      const rName = this.series?.teamRedName || "Red Team"
      await this.startNewSeries(bName, rName)
    },

    async selectChampion(championId) {
      const turn = this.currentTurn
      if (!turn || !this.sessionId) return

      const action = { championId, actionType: turn.type, team: turn.side }

      try {
        const res = await fetch(`http://localhost:8080/api/draft/${this.sessionId}/action`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(action),
        })

        if (!res.ok) {
          const errorData = await res.json()
          console.error("Backend validation failed:", errorData)
          return // Exit early so we don't overwrite state with an error
        }

        this.series = await res.json()
      } catch (error) {
        console.error("Network or parsing error:", error)
      }
    },
  },
  getters: {
      currentTurn(state) {
        if (!state.series || !state.series.games || state.series.games.length === 0) {
          return null
        }

        const game = state.series.games[state.series.games.length - 1]

        const totalActions =
          (game.blueBans?.length || 0) +
          (game.redBans?.length || 0) +
          (game.bluePicks?.length || 0) +
          (game.redPicks?.length || 0)

        return state.turnOrder[totalActions] || null
      },

      isFearlessDisabled: (state) => {
        return (id) => {
          if (!state.series) return false
          const blueBurned = state.series.blueBurnedPicks || []
          const redBurned = state.series.redBurnedPicks || []
          return blueBurned.includes(id) || redBurned.includes(id)
        }
      },

      getPhaseLabel(state) {
        if (!state.series || !state.series.games || state.series.games.length === 0) {
          return "Draft Not Started"
        }

        const game = state.series.games[state.series.games.length - 1]
        const total =
          (game.blueBans?.length || 0) +
          (game.redBans?.length || 0) +
          (game.bluePicks?.length || 0) +
          (game.redPicks?.length || 0)

        if (total < 6) return `Phase 1: Ban ${total + 1}/6`
        if (total < 12) return `Phase 1: Pick ${total - 5}/6`
        if (total < 16) return `Phase 2: Ban ${total - 11}/4`
        if (total < 20) return `Phase 2: Pick ${total - 15}/4`
        return "Draft Complete"
      },
    },
})
