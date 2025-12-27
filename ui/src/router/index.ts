import { createRouter, createWebHistory } from 'vue-router'
import SetupView from '../views/SetupView.vue'
import DraftView from '../views/DraftView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: SetupView },
    { path: '/draft/:id', component: DraftView, props: true }
  ],
})
export default router