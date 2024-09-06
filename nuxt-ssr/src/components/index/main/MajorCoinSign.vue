<template>
  <div class="mobile_highEarningSign_wrap">
    <div class="title">
      <!-- <div class="titIcon"><img :src="`${$static.imgUrl}index/main/majorCoin_icon.svg`" alt="아이콘"></div> -->
      <h1>메이저 코인 SIGN</h1>
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
    <div class="noItems loading" v-else>
      <p><i class="xi-error-o"></i>최근 등록된 싸인이 없습니다</p>
    </div>
  </div>  <!--BestEarningSign_wrap-->
</template>

<script>
export default {
  props: ["majorSigns"],
  data() {
    return {
      items: this.majorSigns.map(item => {
        return {
          ...item,
          url: `/m/${item.mentorId}/${item.subSeqId}`,
          minuteText: this.getMinuteText(item.validateMin, 'ko'),
          minuteTextEng: this.getMinuteText(item.validateMin, 'en'),
          targetEarningRateText: ((item.targetPrice / item.startPrice - 1) * 100).toFixed(2),
          startTimeText: new Date(item.startTime).format("HH:mm"),
          endTimeText: new Date(item.endTime).format("HH:mm"),
        }
      }),
    }
  },
  async mounted() {
    // await this.loadSign();
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
  @import '@/src/components/index/main/css/majorCoinSign.scss';
</style>