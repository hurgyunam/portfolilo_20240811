<template>
  <div class="mobile_introduceMentor_wrap">
    <div class="header">
      <div class="shadow shadow_left" :class="{on: leftShadow}"></div>
      <div class="shadow shadow_right" :class="{on: rightShadow}"></div>
      <ul ref="ul" @scroll="scrollTabMenu">
        <li class="introduceMentorMenu" ref="introduceMentorMenu"
          :class="{active: tabMenuIdx == idx}"
          v-for="(tapMenu, idx) in introduceMentorMenus" :key="idx"
          @click="tabMenuIdx = idx"
        >
          <BtnEffect 
            duration='0.7'
            color='#FB9BA9'
          >
            <div class="inner">
              <div class="MenuIcon">
                <i class="xi-user"></i>
              </div>
              <h1>{{ tapMenu.title }}</h1>
            </div>
          </BtnEffect>
        </li>
      </ul>
    </div>  <!--header-->
    <div class="introduceMentorList">
      <IntroduceMentorBestEarning
        :mentorData="introduceMentorMenus[0].contents"
        :active="tabMenuIdx === 0"
      />
      <IntroduceMentorTotalList
        :mentorData="introduceMentorMenus[1].contents"
        :active="tabMenuIdx === 1"
      />
    </div>  <!--introduceMentorList-->
  </div>
</template>

<script>
import IntroduceMentorBestEarning from '@/src/components/index/main/introduceMentor/IntroduceMentorBestEarning';
import IntroduceMentorTotalList from '@/src/components/index/main/introduceMentor/IntroduceMentorTotalList';
import BtnEffect from '@/src/components/common/BtnEffect';
import axios from 'axios';

export default {
  components: {
    BtnEffect,IntroduceMentorBestEarning,IntroduceMentorTotalList, 
  },
  data() {
    return {
      tabMenuIdx: 0,
      leftShadow: false,
      rightShadow: true,
      introduceMentorMenus: [
        {
          title: '멘토별 최고 수익 예측',
          contents: [
            {path: '/m/homerun/1107', img: `${process.env.VUE_APP_IMG_URL}index/main/introduceMentor/recommendMentor_1.png?version=${new Date().format('yyyyMMdd')}`},
            {path: '/m/dtw/6290', img: `${process.env.VUE_APP_IMG_URL}index/main/introduceMentor/recommendMentor_2.png?version=${new Date().format('yyyyMMdd')}`},
            {path: '/m/bitpapa/18', img: `${process.env.VUE_APP_IMG_URL}index/main/introduceMentor/recommendMentor_3.png?version=${new Date().format('yyyyMMdd')}`},
            {path: '/m/rabbit/5552', img: `${process.env.VUE_APP_IMG_URL}index/main/introduceMentor/recommendMentor_4.png?version=${new Date().format('yyyyMMdd')}`},
          ],
        },
        {
          title: '코싸인 전체 멘토',
          contents: null,
        },
      ],
      
    }
  },
  mounted() {
    this.setTotalMentor();
  },
  methods: {
    async setTotalMentor() {
      // await axios.get(`/api/mentor_page/success_rate/main`)
      // .then(res=> {
      //   this.introduceMentorMenus[1].contents = res.data;
      // })
    },
    scrollTabMenu() {
      const $ul = this.$refs.ul;
      if($ul.scrollWidth - $ul.offsetWidth <= $ul.scrollLeft) {
        this.leftShadow = true;
        this.rightShadow = false;
      } else if($ul.scrollLeft === 0) {
        this.leftShadow = false;
        this.rightShadow = true;
      } else {
        this.leftShadow = true;
        this.rightShadow = true;
      }
    }
  }
}
</script>

<style lang='scss'>
  @import './css/introduceMentor.scss';
</style>