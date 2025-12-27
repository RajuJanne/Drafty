<template>
  <div class="fixed inset-0 z-[100] bg-slate-950 flex items-center justify-center p-6">
    <div class="max-w-2xl w-full bg-slate-900 border border-slate-800 rounded-2xl p-12 shadow-2xl">
      <h2 class="text-4xl font-black text-center mb-12 tracking-tighter uppercase italic text-blue-500">
        Series Configuration
      </h2>

      <div class="space-y-8">
        <div class="grid grid-cols-2 gap-8">
          <div class="space-y-2">
            <label class="text-[10px] font-bold text-blue-400 uppercase tracking-widest">Blue Side</label>
            <input v-model="blueName" class="w-full bg-slate-950 border border-slate-800 p-4 rounded-xl focus:ring-2 ring-blue-500 outline-none text-xl font-bold" />
          </div>
          <div class="space-y-2">
            <label class="text-[10px] font-bold text-red-400 uppercase tracking-widest text-right block">Red Side</label>
            <input v-model="redName" class="w-full bg-slate-950 border border-slate-800 p-4 rounded-xl focus:ring-2 ring-red-500 outline-none text-xl font-bold text-right" />
          </div>
        </div>

        <div class="space-y-4">
          <label class="text-[10px] font-bold text-slate-500 uppercase tracking-widest text-center block">Series Format</label>
          <div class="flex justify-center gap-4">
            <button v-for="len in [1, 3, 5]" :key="len" @click="seriesLength = len"
              :class="seriesLength === len ? 'bg-blue-600 border-blue-400' : 'bg-slate-950 border-slate-800 text-slate-500'"
              class="px-8 py-3 rounded-xl border-2 font-black transition-all hover:scale-105">
              BO{{ len }}
            </button>
          </div>
        </div>

        <button @click="handleLaunch" 
          class="w-full bg-gradient-to-r from-blue-600 to-blue-700 hover:from-blue-500 py-5 rounded-2xl font-black text-xl uppercase tracking-widest transition-all active:scale-95">
          Launch Draft
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useDraftStore } from '../stores/draftStore';

const router = useRouter();
const store = useDraftStore();

const blueName = ref("Blue Side");
const redName = ref("Red Side");
const seriesLength = ref(3);

async function handleLaunch() {
  // Pass the local refs to the store action
  const session = await store.startNewSeries(blueName.value, redName.value, seriesLength.value);
  if (session && session.id) {
    router.push(`/draft/${session.id}`);
  }
}
</script>