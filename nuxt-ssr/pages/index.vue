<template>
  <div class="mobile_indexHome_wrap">
    <LoginForm />
    <!-- <MobHeader class="mHeader" /> -->
    <main>      
      <section id="section1" ref="section1">
          <VisualSlide class="visualSlide"/>
      </section>
      <section id="section2">
        <div class="center">
          <MentorList class="mentorList" :totalMentors="totalMentors" :subscribedMentors="subscribedMentors" />
        </div>
      </section>
      <section id="section3">
        <div class="bestEarningSign_area">
          <div class="center">
            <BestEarningSign class="bestEarningSign" :bestEarningSigns="bestEarningSigns" />
          </div>
        </div>
        <div class="center">
          <ShortValidationSign class="shortValidationSign" :shortSigns="shortSigns" />
          <MajorCoinSign class="majorCoinSign" :majorSigns="majorSigns"/>
        </div>
      </section>
      <section id="section4">
        <TodayMentor/>
      </section>
      <section id="section5">
        <div class="center">
          <RecommendMentor/>
        </div>
      </section>
      <section id="section6">
        <div class="center">
          <TableList :latestSigns="latestSigns" :hitSigns="hitSigns"/>
        </div>
      </section>
    </main>
    <footer>
      <ReferBottom/>
      <Footer/>
    </footer>
  </div>
</template>

<script>
import LoginForm from '@/src/components/common/LoginForm';
// import MobHeader from '@/src/components/common/MobHeader';
import VisualSlide from '@/src/components/index/top/VisualSlide';
import MentorList from '@/src/components/index/main/MentorList';
import BestEarningSign from '@/src/components/index/main/BestEarningSign';
import ShortValidationSign from '@/src/components/index/main/ShortValidationSign';
import MajorCoinSign from '@/src/components/index/main/MajorCoinSign';
import TodayMentor from '@/src/components/index/main/TodayMentor';
import RecommendMentor from '@/src/components/index/main/RecommendMentor';
import TableList from '@/src/components/index/main/TableList';
import ReferBottom from '@/src/components/common/ReferBottom';
import Footer from '@/src/components/common/Footer';

export default {
  name: 'IndexPage',
  components: {
    LoginForm, VisualSlide, MentorList,
    BestEarningSign, ShortValidationSign, MajorCoinSign,
    TodayMentor, RecommendMentor,
    TableList,
    Footer, ReferBottom,
  },
  head() {
      return {
          title: `코인의 모든 싸인 - COSIGN`,
          meta: [
            { hid: 'description', name: 'description', content: `AI와 투자 전문가가 찾아내는 가상화폐 예측 플랫폼` },
            { hid: 'keywords', name: 'keywords', content: `가상화폐,암호화폐,비트코인,이더리움,도지코인,알트코인,코인전망,코인시세,비트코인시세,비트코인예측,투자정보` },
            
            { name: 'og:type', content: `website` },
            { name: 'og:title', content: `코인의 모든 싸인 - COSIGN` },
            { name: 'og:description', content: `AI와 투자 전문가가 찾아내는 가상화폐 예측 플랫폼` },
            { name: 'og:image', content: `https://www.cosign.cc/favicon.png` },
            { name: 'og:url', content: `https://www.cosign.cc/` },
          ]
      }
  },
  async asyncData({$axios}) {
    const totalMentors = (await $axios.get(`/api/mentor_page/success_rate/main`)).data.totalMentorLists;
    const subscribedMentors = (await $axios.post(`/api/main/subscribing_mentor`, {}, {withCredentials: true})).data;
    const bestEarningSigns = (await $axios.get(`/api/main/highest_profit`)).data;
    const shortSigns = (await $axios.get(`/api/main/shortHit_sign`)).data;
    const majorSigns = (await $axios.get(`/api/main/major_coin_sign`)).data;
    const latestSigns = (await $axios.get(`/api/main/latest_forecast_sign`)).data;
    const hitSigns = (await $axios.get(`/api/main/recent_earnings_hit_sign`)).data;

    console.log("baseURL", $axios.defaults.baseURL)

    // console.log(bestEarningSigns);

    return {totalMentors, subscribedMentors, bestEarningSigns, shortSigns, majorSigns, latestSigns, hitSigns}
  },
  mounted() {
    window.addEventListener('scroll', this.headerChange);
  },
  beforeDestroy() {
    window.removeEventListener('scroll', this.headerChange);
  },
  methods: {
    headerChange() {
      let scrollTop = window.pageYOffset || document.documentElement.scrollTop || window.scrollY;
      let changePos = this.$refs.section1.offsetHeight + this.$refs.section1.getBoundingClientRect().top + scrollTop;
      if(scrollTop >= changePos) {
        this.$emit('headerColorChange', false)
      } else {
        this.$emit('headerColorChange', true)
      }
    }
  }
}
</script>

<style lang="scss">
  @import "@/src/css/indexHome.scss";
</style>