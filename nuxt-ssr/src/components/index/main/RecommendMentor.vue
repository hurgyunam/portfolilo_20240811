<template>
  <div class="recommendMentor_wrap">
    <div class="title">
      <div class="title_list_wrap">
        <div class="title_list" ref="title_list" v-for="(mentorListTit, idx) in mentorListData" :key="idx"
          :class="{active: activeListIndex === idx}" @click="activeListIndex = idx">
          <div class="list_inner">
            {{ mentorListTit.listName }}
          </div>
        </div>
        <span class="activeStick" ref="activeStick"></span>
      </div>
    </div>
    <div class="mentorList">
      <div class="swiper_wrap" v-for="(data, idx) in mentorListData" :key="idx">
        <div class="swiper" v-show="activeListIndex === idx">
          <div class="inner" v-for="(mentor,idx2) in data.list" :key="idx2">
            <a :href="mentor.path" :target="mentor.target" v-if="data.list.length > 0">
             <div class="contentImg"><img :src="mentor.img" alt="컨텐츠이미지" @error="onImageError"></div>
            </a>
            <!-- <div class="text">
              <div class="mentor">
                <div class="mentorImg"><img :src="`${imgURLMentor}dtw.png`" alt="멘토이미지"></div>
                <h2>멘토타이틀</h2>
              </div>
              <div class="coin">코인이름</div>
            </div> -->
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    const version = '0.2.15';
    return {
      activeListIndex: 0,
      mentorListData: [
        {listName: '지난주 최고 수익 예측', list: [
          {img: `${this.$static.imgUrl}index/main/introduceMentor/bestEarningMentor_1.png?ver=${version}`, path: 'm/dtw/12250', target: '_self'},
          {img: `${this.$static.imgUrl}index/main/introduceMentor/bestEarningMentor_2.png?ver=${version}`, path: 'm/homerun/2872', target: '_self'},
          {img: `${this.$static.imgUrl}index/main/introduceMentor/bestEarningMentor_3.png?ver=${version}`, path: 'm/homerun/2822', target: '_self'},
        ]},
        {listName: '연속 적중 TOP 멘토', list: [
          {img: `https://kr.object.ncloudstorage.com/cosign/project-images/cosign-view/index/main/introduceMentor/continuHit_1.png?ver=${version}`, path: 'm/bottom', target: '_self'},
          {img: `https://kr.object.ncloudstorage.com/cosign/project-images/cosign-view/index/main/introduceMentor/continuHit_2.png?ver=${version}`, path: 'm/dtw', target: '_self'},
          {img: `https://kr.object.ncloudstorage.com/cosign/project-images/cosign-view/index/main/introduceMentor/continuHit_3.png?ver=${version}`, path: 'm/hoonbotlight', target: '_self'},
        ]},
        {listName: '카드 뉴스', list: [
          {img: `${this.$static.imgUrl}index/main/introduceMentor/cardNews_1.png?ver=${version}`, path: 'https://www.instagram.com/p/CfDX8kGPlN7/', target: '_blank'},
          {img: `${this.$static.imgUrl}index/main/introduceMentor/cardNews_2.png?ver=${version}`, path: 'https://www.instagram.com/p/CfBNmb4PlTB/', target: '_blank'},
          {img: `${this.$static.imgUrl}index/main/introduceMentor/cardNews_3.png?ver=${version}`, path: 'https://www.instagram.com/p/Ce26cR-OJqB/', target: '_blank'},
        ]},
      ],
    }
  },
  mounted() {
    // this.setBestEarningMentor();
    this.moveStick();
  },
  watch: {
    activeListIndex() {
      this.moveStick();
    }
  },
  methods: {
    setBestEarningMentor() {
      this.mentorListData[0].list = [
        {name: 'homerun', path: '/m/homerun/1107', img: `${process.env.VUE_APP_IMG_URL}index/main/introduceMentor/bestEarningMentor_1.png`},
        {name: 'dtw', path: '/m/dtw/6290', img: `${process.env.VUE_APP_IMG_URL}index/main/introduceMentor/bestEarningMentor_2.png`},
        {name: 'bitpapa', path: '/m/bitpapa/18', img: `${process.env.VUE_APP_IMG_URL}index/main/introduceMentor/bestEarningMentor_3.png`},
        {name: 'rabbit', path: '/m/rabbit/5552', img: `${process.env.VUE_APP_IMG_URL}index/main/introduceMentor/bestEarningMentor_3.png`},
      ]
    },
    moveStick() {
      this.$nextTick(()=> {
        let activeStick = this.$refs.activeStick;
        let title_list = this.$refs.title_list[this.activeListIndex];

        activeStick.style.width = `${title_list.offsetWidth}px`;
        activeStick.style.transform = `translate3d(${title_list.offsetLeft - 16}px, 0, 0)`; //부모에서 padding 양쪽에 16줬기에
        // activeStick.style.width = (title_list.offsetWidth === 0) ? '56px':`${title_list.offsetWidth}px`;
        // activeStick.style.transform = `translate3d(${title_list.offsetLeft}px, 0, 0)`;
      })
    },
    onImageError(e) {
      e.target.src = `${process.env.VUE_APP_IMG_URL_Mentor}default.png`;
    },
  }
}
</script>

<style lang='scss'>
  @import './css/recommendMentor.scss';
</style>