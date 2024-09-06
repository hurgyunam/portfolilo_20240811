<template>
  <div class="mobile_bestExchangeSign_wrap">
    <div class="title">
      <!-- <div class="titIcon"><img :src="`${$static.imgUrl}index/main/shortTime_icon.svg`" alt="아이콘"></div> -->
      <h1>코인 단타 SIGN</h1>
    </div>
    <div class="slide_wrap" v-if="this.items.length > 0">
      <div class="slides">
        <a :href="item.url" v-for="(item, idx) in items" :key="idx">
          <div class="slide" ref="slide">
            <div class="validation">
              <img :src="`${$static.imgUrl}index/main/validation_icon.svg`" alt="유효기간 아이콘">
              <p v-html="item.minuteText"></p>
            </div>
            <div class="mentor">
              <div class="marquee" ref="marquee_wrap">
                <h2 ref="marquee">
                  {{ item.mentorTitle }}
                </h2>
              </div>
              <div class="mentorImg"><img :src="item.mentorImg" alt="멘토이미지"></div>
            </div>
            <div class="earning">
              <h2>목표수익률</h2>
              <p>{{ item.targetEarningRateText }}<span>%</span></p>
            </div>
            <div class="coin">
              <!-- <div class="coinImg"><img :src="item.marketImg" alt="코인이미지"></div> -->
              <h2>{{ item.market }}</h2>
              <!-- <p>{{ item.marketCode.substr(4) }}</p> -->
            </div>
          </div>
        </a>
      </div>
    </div>  <!--slide_wrap-->
    <div class="noItems" v-else :class="{loading: !isLoading}">
      <p v-if="!isLoading">로딩중 입니다.</p>
      <p v-else><i class="xi-error-o"></i>최근 등록된 싸인이 없습니다</p>
    </div>
  </div>  <!--BestEarningSign_wrap-->
</template>

<script>
import { index } from '@/src/mixin/index.js';

export default {
  mixins: [index],
  props: ["shortSigns"],
  data() {
    return {
      isLoading: false,
      items: this.shortSigns.map(item => {
        return {
          ...item,
          url: `/m/${item.mentorId}/${item.subSeqId}`,
          minuteText: this.getMinuteText(item.validateMin, 'ko'),
          minuteTextEng: this.getMinuteText(item.validateMin, 'en'),
          startTimeText: new Date(item.startTime).format('HH:mm'),
          endTimeText: new Date(item.startTime).addDate('minute', item.validateMin-1).format('HH:mm'),
          targetEarningRateText: ((item.targetPrice / item.startPrice - 1) * 100).toFixed(2),
        }
      })
    }
  },
  methods: {
    getMinuteText(minute) {
      if(minute / 60 < 1) {
        return `${minute}<span>min</span>`;
      } else {
        return `${Math.ceil(minute/60)}<span>hour</span>`;
      }
    }
  }
}
</script>

<style lang='scss'>
  @import '@/src/components/index/main/css/shortValidationSign.scss';
</style>