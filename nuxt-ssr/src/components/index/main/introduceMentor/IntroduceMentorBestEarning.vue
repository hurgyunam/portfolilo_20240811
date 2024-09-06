<template>
  <div class="IntroduceMentorBestEarning_wrap clearFix" v-show="active">
    <div v-for="(img,idx) in mentorData" :key="idx">
      <a :href="img.path">
        <img :src="img.img" alt="추천멘토이미지" usemap="#location">
      </a>
    </div>
    <div class="noItems" v-if="mentorData.length <= 0">
      <p><i class="xi-error-o"></i>최근 등록된 멘토가 없습니다</p>
    </div>
    <p>*2022년 05월 16일 09시 기준</p>
  </div>
</template>

<script>
export default {
  props: ['mentorData', 'active'],
  data() {
    return {
      // today: new Date().format('yyyy년 MM월 dd일')
    }
  },
}
</script>

<style lang='scss'>
  @import './css/introduceMentorBestEarning.scss';
</style>