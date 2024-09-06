<template>
  <div class="newPredictSign_wrap">
    <a :href="item.url">
      <div class="inner">
        <div class="coin">
          <!-- <div class="coinImg"><img :src="item.marketImg" alt="코인 이미지"></div> -->
          <h1>{{ item.market }}</h1>
        </div>
        <div class="earning">
          {{ item.targetEarningRateText }}%
        </div>
        <div class="validation">
          {{ item.minuteText }}
        </div>
        <div class="mentor">
          <div class="mentorImg"><img :src="item.mentorImg" alt="멘토 이미지"></div>
          <div class="marquee" ref="marquee_wrap">
            <h1 ref="marquee">{{ item.mentorTitle }}</h1>
          </div>
        </div>
      </div>
    </a>
  </div>
</template>

<script>
// import { mixin_indexPageMixin } from '@/src/mixin/mixin_indexPage'
export default {
  props: ['item'],
  // mixins: [mixin_indexPageMixin],
  data() {
    return {
      
    }
  },
}
</script>

<style lang='scss'>
  @import '@/src/components/index/main/css/newPredictSign.scss';
</style>