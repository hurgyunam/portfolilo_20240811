<template>
  <div class="todayMentor_wrap">
    <div class="swiper" v-swiper:mySwiper="swiperOption" ref="swiper">
      <div class="swiper-wrapper">
        <div class="swiper-slide" v-for="(list,idx) in todayMentorList" :key="idx">
          <div class="inner">
            <a :href="list.path">
              <img :src="list.img" alt="투데이멘토이미지">
            </a>
          </div>
        </div>
      </div>
    </div>
    <div class="swiper-pagination todayMentor_pagination" slot="pagination"></div>
  </div>
</template>

<script>

export default {
  components: {
     
  },
  data() {
    let st = null;
    const version = '0.2.15'
    return {
      todayMentorList: [
        {color: '#F38683', img: `https://kr.object.ncloudstorage.com/cosign/project-images/cosign-view/index/main/todayMentor/mobile_todayMentor_1.png?ver=${version}`, path: 'm/rabbit'},
        {color: '#03204E', img: `https://kr.object.ncloudstorage.com/cosign/project-images/cosign-view/index/main/todayMentor/mobile_todayMentor_2.png?ver=${version}`, path: 'm/dtw'},
        {color: '#4AC2BE', img: `https://kr.object.ncloudstorage.com/cosign/project-images/cosign-view/index/main/todayMentor/mobile_todayMentor_3.png?ver=${version}`, path: 'm/homerun'},
        {color: '#00A152', img: `https://kr.object.ncloudstorage.com/cosign/project-images/cosign-view/index/main/todayMentor/mobile_todayMentor_4.png?ver=${version}`, path: 'm/bottom'},
        {color: '#7B41E6', img: `https://kr.object.ncloudstorage.com/cosign/project-images/cosign-view/index/main/todayMentor/mobile_todayMentor_5.png?ver=${version}`, path: 'm/news'},
        {color: '#E26440', img: `https://kr.object.ncloudstorage.com/cosign/project-images/cosign-view/index/main/todayMentor/mobile_todayMentor_6.png?ver=${version}`, path: 'm/hoonbotlight'},
        {color: '#BD253E', img: `https://kr.object.ncloudstorage.com/cosign/project-images/cosign-view/index/main/todayMentor/mobile_todayMentor_7.png?ver=${version}`, path: 'm/bitpapa'},
        {color: '#3178E2', img: `https://kr.object.ncloudstorage.com/cosign/project-images/cosign-view/index/main/todayMentor/mobile_todayMentor_8.png?ver=${version}`, path: 'm/rocket'},
        {color: '#2AA9E0', img: `https://kr.object.ncloudstorage.com/cosign/project-images/cosign-view/index/main/todayMentor/mobile_todayMentor_9.png?ver=${version}`, path: 'm/sns'},
        {color: '#2C1B72', img: `https://kr.object.ncloudstorage.com/cosign/project-images/cosign-view/index/main/todayMentor/mobile_todayMentor_10.png?ver=${version}`, path: 'm/global'},
      ],
      activeIndex: 0,
      swiperOption: {
        speed: 600,
        loop: true,
        touchRatio: 1.07,
        threshold: true,
        resistance : true,
        resistanceRatio: 1,
        touchReleaseOnEdges: true,
        pagination: {
          el: '.todayMentor_pagination',
          clickable: true,
          // type: "fraction",
        },
        on: {
          touchStart() {
            st = new Date().valueOf();
            this.touches.diff = 0;
          },
          touchEnd() {
            let diffTime = new Date().valueOf() - st;
            if(!this.touches.diff) {
              this.params.speed = 600;
            } else {
              if(diffTime >= 900) diffTime = 800;
              else if(diffTime <= 100) diffTime = 100;
              this.params.speed = diffTime * 1.6;
            }
          },
          transitionEnd() {
            this.params.speed = 600;
          },
        }
      },
    }
  },
  mounted() {
    this.randomContent();
  },
  watch: {
    activeIndex(idx) {
      this.swiper.slideTo(idx,0);
    }
  },
  computed: {
    swiper() {
      return this.$refs.swiper.swiper;
    }
  },
  methods: {
    randomContent() {
      let randomIndex = Math.floor(Math.random()*this.todayMentorList.length+1);
      this.activeIndex = randomIndex;
    }
  }
}
</script>

<style lang='scss'>
  @import './css/todayMentor.scss';
</style>