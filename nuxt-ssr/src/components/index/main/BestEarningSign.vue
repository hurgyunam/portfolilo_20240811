<template>
  <div class="mobile_bestEarningSign_wrap">
    <div class="title">
      <!-- <div class="titIcon"><img :src="`${$static.imgUrl}index/main/bestEarning_icon.svg`" alt="아이콘"></div> -->
      <h1>최고 수익 SIGN</h1>
    </div>
    <div class="slide_wrap" v-if="items.length > 0">
      <div class="slides">
        <a :href="item.url" v-for="(item, idx) in items" :key="idx">
          <div class="slide" ref="slide"
            :class="{ranker: item.rank <= 3}"
          >
            <div class="ranking">{{ item.rank }}위</div>
            <div class="mentor">
              <div class="marquee" ref="marquee_wrap">
                <h2 ref="marquee">
                  {{ item.mentorTitle }}
                </h2>
              </div>
              <div class="mentorImg"><img :src="item.mentorImg" alt="멘토이미지"></div>
            </div>
            <div class="earning">
              <!-- <p class="validation">{{ item.validationText }}</p> -->
              <h2>
                {{ item.earningRateText }}<span>%</span>
              </h2>
              <p>수익</p>
            </div>
            <div class="coin">
              <!-- <div class="coinImg"><img :src="item.marketImg" alt="코인이미지"></div> -->
              <h2>{{ item.market }}</h2>
            </div>
          </div>
        </a>
      </div>
    </div>  <!--slide_wrap-->
    <!-- <div class="noItems" v-else :class="{loading: !isLoading}"> -->
    <div class="noItems" v-else>
      <!-- <p v-if="!isLoading">로딩중 입니다.</p> -->
      <p><i class="xi-error-o"></i>최근 등록된 싸인이 없습니다</p>
    </div>
  </div>  <!--mobile_bestEarningSign_wrap-->
</template>

<script>
export default {
  props: ["bestEarningSigns"],
  data() {
    return {
      items: (this.bestEarningSigns.highestProfitSignRes) ? this.bestEarningSigns.highestProfitSignRes.map(item => {
        return {
          ...item,
          url: `/m/${item.mentorId}/${item.subSeqId}`,
          earningRateText: item.earningRate.toFixed(2),
          validationText: this.getMinuteText(item.validateMin),
        }
      }) : [],
      updateTime: new Date(this.bestEarningSigns.curTime).format('MM-dd HH:mm'),
      interval: null,
    }
  },
  computed: {
  },
  methods: {
    getMinuteText(minute) {
      if(minute / 60 < 1) {
        return `${minute}분`;
      } else {
        return `${Math.ceil(minute/60)}시간`;
      }
    }
  }
}
</script>

<style lang='scss'>
  @import '@/src/components/index/main/css/bestEarningSign.scss';
</style>