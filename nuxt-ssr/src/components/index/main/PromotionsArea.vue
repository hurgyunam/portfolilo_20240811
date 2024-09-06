<template>
  <div class="promotionArea_wrap">
    <div class="swiper" v-swiper:mySwiper="swiperOption" ref="swiper">
      <div class="swiper-wrapper">
        <div class="swiper-slide" v-for="(promotion,idx) in promotionData" :key="idx">
          <div class="inner">
            <div class="content">
              <div class="title">
                <div class="mentor1 mentor">
                  <div class="mentorImg"><img :src="promotion.mentorImg" alt="멘토이미지"></div>
                  <p>{{ promotion.subTit }}</p>
                  <h1>{{ promotion.mentor }}
                    <!-- <span v-if="idx === 7">{{ `& ${promotion.mentor2}` }}</span> -->
                    <span v-if="idx === 6">{{ `& ${promotion.mentor2}` }}</span>
                  </h1>
                </div>
                <!-- <div class="mentor2 mentor" v-if="idx === 3">
                  <p>{{ promotion.subTit2 }}</p>
                  <h1>{{ promotion.mentor2 }}</h1>
                  <div class="mentorImg"><img :src="promotion.mentorImg2" alt="멘토이미지"></div>
                </div> -->
              </div>  <!--title-->
              <div class="desc">
                <p v-html="promotion.desc"></p>
                <div class="descImg"><img :src="promotion.contentImg" alt="컨텐츠이미지"></div>
              </div>
              <div class="point">
                <ul>
                  <li>
                    <p>Point 1</p>
                    <span v-html="promotion.point1"></span>
                  </li>
                  <li>
                    <p>Point 2</p>
                    <span v-html="promotion.point2"></span>
                  </li>
                  <li v-if="promotion.point3 !== ''">
                    <p>Point 3</p>
                    <span v-html="promotion.point3"></span><i>({{ promotion.postTime }} 기준)</i>
                  </li>
                </ul>
              </div>
            </div>
            <div class="viewMore">
              <a :href="`/m/${promotion.url}`">
                <!-- <p>{{ promotion.mentor }} <span v-if="[0,1,2,4,5,6,8].includes(idx)">예측</span> 보기<i class="xi-arrow-right"></i></p> -->
                <p>{{ promotion.mentor }} <span v-if="[0,1,2,3,4,5,7].includes(idx)">예측</span> 보기<i class="xi-arrow-right"></i></p>
              </a>
              <!-- <a :href="`/m/${promotion.url2}`" v-if="idx === 3 || idx === 7"> -->
              <a :href="`/m/${promotion.url2}`" v-if="idx === 6">
                <!-- <p>{{ promotion.mentor2 }} <span v-if="[0,1,2,4,5,6,8].includes(idx)">예측</span> 보기<i class="xi-arrow-right"></i></p> -->
                <p>{{ promotion.mentor2 }} <span v-if="[0,1,2,3,4,5,7].includes(idx)">예측</span> 보기<i class="xi-arrow-right"></i></p>
              </a>
            </div>
          </div>
        </div>
      </div>      
    </div>
    <div class="swiper-pagination promotion_pagination" slot="pagination"></div>
  </div>
</template>

<script>

export default {
  components: {
     
  },
  data() {
    return {
      today: new Date().format('yyyy-MM-dd'),
      activeIndex: 0,
      promotionData: [
        {url: 'dtw',subTit: '검증된 평행이론', mentor: '차트 유사 패턴 AI', mentorImg: `${process.env.VUE_APP_IMG_URL_Mentor}dtw.png`, desc: 'DTW  분석 방법을 사용해 실시간으로 현재 패턴과 유사한 과거 패턴을 탐색하고 그 결과를 분석하여 코인 시세를 예측합니다', postTime: '2022-05-04', point1: '과거 패턴들의 유형을 이용한 분석 기법으로 <b>예측 시세 확인</b> 가능', point2: 'COSIGN 멘토들 중 가장 <b>높은 빈도의 예측</b>을 제공', point3: '<b>최고 수익률 14%, 수익 적중률 53.5%</b> 달성',contentImg: `${process.env.VUE_APP_IMG_URL}index/main/todayMentor_dtw.png`},
        {url: 'bottom', subTit: '단타매니아를 위한', mentor: '바닥 포착', mentorImg: `${process.env.VUE_APP_IMG_URL_Mentor}bottom.png`, desc: '실시간으로 단기 바닥을 포착하여 여러분에게 매수 시점을 알려주고 하락장에서 대응이 가능한 AI 예측 모델입니다', postTime: '2022-05-04',point1: '예측 시간이 가장 짧은 15분, 30분으로 <b>초단타</b> 투자 가능',point2:'모든 코인을 예측하여 분산 투자하는 사람들을 위한 맞춤 모델',point3: '<b>최고 수익률 4.2%, 수익 적중률 49%</b> 달성',contentImg: `${process.env.VUE_APP_IMG_URL}index/main/todayMentor_bottom.png`},
        {url: 'news', subTit: '속보, 코인 예측 발표', mentor: 'Bull News AI', mentorImg: `${process.env.VUE_APP_IMG_URL_Mentor}news.png`, desc: '신뢰할 수 있는 매체에서 코인 뉴스를 수집하고 비트코인 가격에 영향을 주는 키워드를 뽑아 AI로 분석하여 가격을 예측합니다', postTime: '2022-05-04',point1: '코인 뉴스 검색할 시간 절약하고 예측 확인에 올인',point2:'<b>비트코인</b>만 투자하는 사람들을 위한 맞춤 모델',point3: '뉴스의 재발견! <b>수익 적중률 45.8%</b> 달성',contentImg: `${process.env.VUE_APP_IMG_URL}index/main/todayMentor_news.png`},
        // {url: 'updown', url2: 'volume', subTit: 'UP - DOWN', subTit2: '& Volume UP', mentor: '등락폭 리샘플링', mentor2: '거래량 리샘플링', mentorImg: `${process.env.VUE_APP_IMG_URL_Mentor}updown.png`, mentorImg2: `${process.env.VUE_APP_IMG_URL_Mentor}volume.png`, desc: '등락폭 리샘플링 & 거래량 리샘플링 멘토들의 특별한 예측! 각 멘토들이 세운 특별한 기준과 인공지능이 패턴을 파악하여 한층 더 정확한 예측을 제공합니다.', postTime: '2022-05-04', point1: '멘토별 기준으로 캔들을 <b>리샘플링</b>하여 <b>기술지표</b>로 <b>변환</b>', point2:'<b>캔들</b> & <b>기술지표</b> & <b>AI</b>의 완벽한 콜라보', point3: '',contentImg: `${process.env.VUE_APP_IMG_URL}index/main/todayMentor_updown&volume.png`},
        {url: 'sns', subTit: '톡톡한 예측', mentor: 'Tweets Keywords AI', mentorImg: `${process.env.VUE_APP_IMG_URL_Mentor}sns.png`, desc: '누구나 접근이 쉽고 즐겨보던 소셜 미디어에서 코인 정보까지 얻을 수 있는 기회! AI가 코인 트윗들을 분석하여 가격을 예측합니다.', postTime: '2022-05-04', point1: '소셜 미디어에서 소통부터 코인 예측까지 일석이조',point2:'<b>비트코인</b>에 관심이 많은 사람들을 위한 맞춤 모델', point3: '스마트한 예측 <b>수익 적중률 60%</b> 달성',contentImg: `${process.env.VUE_APP_IMG_URL}index/main/todayMentor_sns.png`},
        {url: 'global', subTit: '전세계 투자 정복', mentor: '글로벌 지표 AI', mentorImg: `${process.env.VUE_APP_IMG_URL_Mentor}global.png`, desc: '전세계 경제를 확인할 수 있는 지표들의 집합<br>10여 종의 경제 지표 데이터를 이용하여 코인 시세를 예측하는 AI 모델입니다.', postTime: '2022-05-04', point1: '글로벌 지표 AI에서만 가능한 10여 종 경제지표 수집',point2:'<b>비트코인</b> 한 종목에 올인하는 사람들을 위한 맞춤 모델', point3: '<b>최고 수익률 1.9%, 수익 적중률 51.2%</b> 달성',contentImg: `${process.env.VUE_APP_IMG_URL}index/main/todayMentor_global.png`},
        {url: 'rocket', subTit: '롤러코스터 탑승', mentor: '급등 포착', mentorImg: `${process.env.VUE_APP_IMG_URL_Mentor}rocket.png`, desc: '코인 가격이 급등할 때를 포착하여 여러분에게 예측을 제공합니다.<br>급등 포착 멘토와 같이 상승장으로 출발해 보세요.', postTime: '2022-05-04', point1: '<b>급등</b>하는 <b>시점</b>을 <b>포착</b>하여 수익률을 더 높게',point2:'다양한 코인 종목에 관심있는 사람들을 위한 맞춤 모델', point3: '상승장으로 가는 길 <b>최고 수익률 5.2%</b> 달성',contentImg: `${process.env.VUE_APP_IMG_URL}index/main/todayMentor_rocket.png`},
        {url: 'hoonbot',url2: 'hoonbotlight', subTit: '오늘 투자는 밝음', mentor: 'AI 훈봇', mentor2: 'AI 훈봇 라이트', mentorImg: `${process.env.VUE_APP_IMG_URL_Mentor}hooni.png`, desc: 'AI와 차트분석을 이용하는 AI 훈봇과 조건을 간소화하여 예측 빈도를 높인 AI 훈봇 라이트로 구분하여 코인 가격을 예측합니다.', postTime: '2022-05-04', point1: '<b>AI 훈봇</b>은 <b>메이저 코인</b> 위주로 보수적이고 안전한 예측 제공',point2:'<b>AI 훈봇 라이트</b>는 <b>알트코인</b> 위주로 다양한 종목의 예측 제공', point3: 'AI 훈봇 라이트 <b>수익 적중률 67.7%</b> 1위 달성',contentImg: `${process.env.VUE_APP_IMG_URL}index/main/todayMentor_hoonbot.png`},
        {url: 'bitpapa', subTit: '예측의 아버지', mentor: '비트파파', mentorImg: `${process.env.VUE_APP_IMG_URL_Mentor}youngmin.png`, desc: '일정한 리듬으로 반복되는 흐름에서 패턴을 찾아 차트의 미래를 예측하여 수익을 극대화 하는 전략을 이용하는 휴먼 모델입니다.', postTime: '2022-05-04', point1: '<b>비트코인</b>부터 <b>알트코인</b>까지 다양한 종목 예측',point2:'<b>엘리엇 파동이론</b>과 보조지표의 만남으로 최고의 예측 제공', point3: '<b>최고 수익률 8%, 수익 적중률 53.3%</b> 달성',contentImg: `${process.env.VUE_APP_IMG_URL}index/main/todayMentor_bitpapa.png`},
      ],
      swiperOption: {
        autoHeight: true,
        speed: 400,
        loop: true,
        // initialSlide: 0,
        // parallax: true,
        slidesPerView: 1,
        spaceBetween: 40,
        touchRatio: 0.8,
        pagination: {
          el: '.promotion_pagination',
          clickable: true,
          // type: "fraction",
        },
        on: {
          // init() {
          // }
        }
      },
    }
  },
  mounted() {
    this.randomContent();
    // const buySuccessRateGraph = this.$refs.buySuccessRateGraph;
    // const averageEarningRateGraph = this.$refs.averageEarningRateGraph;
    // let i = this.promotions.length;

    // gsap.registerPlugin(ScrollTrigger);
    // while(i > 0) {
    //   i--;
    //   gsap.to(buySuccessRateGraph[i], {
    //     scrollTrigger: buySuccessRateGraph[i],
    //     width: `${this.promotions[i].successRate}%`,
    //     duration: 2, 
    //   })
    //   gsap.to(averageEarningRateGraph[i], {
    //     scrollTrigger: averageEarningRateGraph[i],
    //     width: `${this.promotions[i].averageEarning}%`,
    //     duration: 2,
    //   })
    // }
    
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
      let randomIndex = Math.floor(Math.random()*this.promotionData.length);
      this.activeIndex = randomIndex;
    }
  }
}
</script>

<style lang='scss'>
  @import '@/src/components/index/main/css/promotionArea.scss';
</style>