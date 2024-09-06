<template>
  <div class="tableList_wrap">
    <div class="title">
      <div class="title_list_wrap">
        <div class="title_list" ref="title_list" v-for="(mentorListTit, idx) in mentorListData" :key="idx"
          :class="{active: activeListIndex === idx}" @click="activeListIndex = idx">
          {{ mentorListTit.listName }}
        </div>
        <span class="activeStick" ref="activeStick"></span>
      </div>
    </div>
    <div class="mentorList">
      <div class="swiper_wrap">
        <div class="swiper" v-swiper:mySwiper="swiperOption" ref="swiper">
          <div class="swiper-wrapper">
            <div class="swiper-slide">
              <div class="inner">
                <NewPredictSignList class="newPredictSignList"
                  :items="mentorListData[0].list"
                />
              </div>
            </div>
            <div class="swiper-slide">
              <div class="inner">
                <HitSignList class="hitSignList"
                  :items="mentorListData[1].list"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// import axios from 'axios';
import NewPredictSignList from '@/src/components/index/main/NewPredictSignList';
import HitSignList from '@/src/components/index/main/HitSignList'

export default {
  components: {
     NewPredictSignList, HitSignList,
  },
  props: ["latestSigns", "hitSigns"],
  data() {
    const latestSigns = this.latestSigns.map(item => {

      return {
        ...item,
        url: `/m/${item.mentorId}/${item.subSeqId}`,
        targetEarningRateText: ((item.targetPrice / item.startPrice - 1) * 100).toFixed(2) * 1,
        startTimeText: new Date(item.startTime).format("yyyy-MM-dd HH:mm"),
        endTime: new Date(item.startTime).addDate('minute', item.validateMin).format('yyyy-MM-ddTHH:mm'),
        minuteText: this.getMinuteText(item.validateMin),
      }
    })
    
    const hitSigns = this.hitSigns.map(item => {
      return {
        ...item,
        url: `/m/${item.mentorId}/${item.subSeqId}`,
        earningRateText: (item.earningRate).toFixed(2),
        startTimeText: new Date(item.startTime).format("MM-dd HH:mm"),
        endTimeText: new Date(item.endTime).format("MM-dd HH:mm"),
        minuteText: this.getMinuteText(item.validateMin),
      }
    })

    return {
      activeListIndex: 0,
      hoverTranslateY: 0,
      mentorListData: [
        {listName: '최근 등록 SIGN', list: latestSigns},
        {listName: '최근 수익 적중 SIGN', list: hitSigns},
      ],
      swiperOption: {
        speed: 600,
        spaceBetween: 20,
        // slidesPerView: 10,
        autoHeight: true,
        allowTouchMove: false,
        // touchRatio: 1,
        navigation: {
          nextEl: '.tableList-button-next',
          prevEl: '.tableList-button-prev'
        },
        on: {
          init() {
          },
        }
      },
    };
  },

  mounted() {
    this.moveStick();
  },
  watch: {
    activeListIndex(val) {
      this.moveStick();
      this.swiper.slideTo(val, 600);
    }
  },
  computed: {
    swiper() {
      return this.$refs.swiper.swiper;
    }
  },
  methods: {
    getMinuteText(minute) {
      if(minute / 60 < 1) {
        return `${minute}분`;
      } else {
        return `${Math.ceil(minute/60)}시간`;
      }
    },
    moveStick() {
      this.$nextTick(()=> {
        let activeStick = this.$refs.activeStick;
        let title_list = this.$refs.title_list[this.activeListIndex];

        activeStick.style.width = `${title_list.offsetWidth}px`;
        activeStick.style.transform = `translate3d(${title_list.offsetLeft}px, 0, 0)`;
      })
    },
    changeHoverTop(val) {
      this.hoverTranslateY = val;
    },
    onImageError(e) {
      e.target.src = `${process.env.VUE_APP_IMG_URL_Mentor}default.png`;
    },
  },
}
</script>

<style lang='scss'>
  @import './css/tableList.scss';
</style>