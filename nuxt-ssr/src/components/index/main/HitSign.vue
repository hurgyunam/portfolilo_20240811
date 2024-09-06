<template>
  <div class="hitSign_wrap">
    <a :href="item.url">
      <div class="inner">
        <div class="coin">
          <!-- <div class="coinImg"><img :src="item.marketImg" alt="코인 이미지"></div> -->
          <h1>{{ item.market }}</h1>
        </div>
        <div class="earning">
          {{ item.earningRateText }}%
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
}
</script>

<style lang='scss'>
  @import './css/hitSign.scss';
</style>