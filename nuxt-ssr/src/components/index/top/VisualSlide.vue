<template>
  <div class="mobile_visualSlide_wrap">
    <div class="swiper" ref="swiper" v-swiper:mySwiper="swiperOption">
      <div class="swiper-wrapper">
        <div class="swiper-slide" v-for="(data,idx) in slideData" :key="idx">
          <a class="router_wrap" :href="data.path" :target="data.target" :style="{background: data.backColor}"
            data-swiper-parallax-x="78%">
            <div class="inner">
              <div class="textContent">
                <h2>{{ data.sub }}</h2>
                <h1 v-html="data.title"></h1>
                <p>{{ data.dec1 }}</p>
                <p v-if="data.dec2">{{ data.dec2 }}</p>
                <p v-if="data.dec3">{{ data.dec3 }}</p>
                <!-- <button>{{ data.att }}</button> -->
              </div>
              <div class="slideImg">
                <img :src="`${$static.imgUrl}index/top/${data.imgUrl}?ver=${version}`" alt="슬라이드이미지">
              </div>
            </div>
          </a>
        </div>
      </div>
      <div class="swiper-pagination index_visualSlide_pagination" slot="pagination"></div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    let st = null;
    const that = this;
    return {
      version: '0.2.13',
      slideData: [
        {sub: 'COSIGN', title: '코인 리딩방에 속지 말고<br>진짜 전문가 AI를 믿어라', dec1: '전문가인 척 투자자들을 속이는 코인 리딩방에서 벗어나세요', dec2: '진짜 코인 전문가인 AI가 시세를 예측합니다',
        att: '진짜 AI가 예측하는 코인 시세 보러 가기', imgUrl: 'mobile_visualSlide_1.png', backColor: '#0D1C80', path: '/m/news', target: '_self'},
        {sub: 'COSIGN', title: '이름만 AI?<br>코싸인은 진짜 AI', dec1: '코싸인이 직접 개발한 AI로 예측하는 코인 예측', dec2: '어떻게 만든 AI 모델인지 다 공개해드립니다!',
        att: '문의하러 가기', backColor: '#916EFF', imgUrl: 'mobile_visualSlide_2.png', path: '/m/global?popup=true', target: '_self'},
        {sub: 'BETA 이벤트', title: 'AI가 예측하는 코인추천<br>모든 SIGN 무료 공개', dec1: 'BETA 기간동안 제공되는 특별한 기회', dec2: 'COSIGN 서비스에서 모든 코인 예측 SIGN을 무료로 공개합니다',
        att: '회원가입하러 가기', backColor: '#676DF7', imgUrl: 'mobile_visualSlide_3.png', path: '/m/rabbit', target: '_self'},
        {sub: '멘토 상시 모집', title: 'SIGN보고<br>SIGN낼래?', dec1: '시세 예측 오픈 플랫폼 COSIGN에서', dec2: '함께 코인 시세를 예측 할 멘토를 모집합니다',
        att: '멘토 등록하러 가기', imgUrl: 'mobile_visualSlide_4.png', backColor: '#F7B300', path: 'https://docs.google.com/forms/d/e/1FAIpQLSdpDxnBRhfH6QoAcQc86Jqwqa7f0xXXsTQULQxGjoKyu7J6GA/viewform', target: '_blank'},
      ],
      swiperHintFlag: false,
      swiperOption: {
        parallax: true,
        // longSwipesRatio: 0.6,
        // longSwipesMs: 500,
        threshold: true,
        resistance : true,
        resistanceRatio: 1,
        touchReleaseOnEdges: true,  //기본값false
        // updateOnImagesReady: true,
        speed: 600,
        loop: true,
        autoplay: {
          delay: 4000,
          disableOnInteraction: false,
        },
        touchRatio: 1.07,
        pagination: {
          el: '.index_visualSlide_pagination',
          clickable: true,
        },
        on: {
          init() {
            that.isShowSwiper();  //맨처음로드시 실행(스크롤전)
            window.addEventListener('scroll', that.isShowSwiper);
          },
          // imagesReady() {
          //   this.el.classList.remove('loading');
          //   this.autoplay.start();
          // },
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
  beforeDestroy() {
    window.removeEventListener('scroll', this.isShowSwiper);
  },
  watch: {
    swiperHintFlag(val) {
      this.hintBrowser(val);
    }
  },
  methods: {
    isShowSwiper() {
      this.$nextTick(()=> {
        let swiperWrap = this.$refs.swiper.swiper.wrapperEl;
        let scrollHeight = window.pageYOffset || window.scrollY || document.documentElement.scrollTop
        let elStartPos = swiperWrap.getBoundingClientRect().top + window.pageYOffset;
        elStartPos = elStartPos - window.innerHeight < 0 ? 0 : elStartPos - window.innerHeight;
        let elEndPos = swiperWrap.getBoundingClientRect().top + window.pageYOffset + swiperWrap.offsetHeight;
        
        if(scrollHeight > elEndPos || scrollHeight < elStartPos) this.swiperHintFlag = false;
        else this.swiperHintFlag = true;
      })
    },
    hintBrowser(flag) {
      let swiperWrap = this.$refs.swiper.swiper.wrapperEl;
      swiperWrap.style.willChange = flag ? 'transform' : 'auto';
    },
  }
}
</script>

<style lang='scss'>
  @import '@/src/components/index/top/css/visualSlide.scss';
</style>