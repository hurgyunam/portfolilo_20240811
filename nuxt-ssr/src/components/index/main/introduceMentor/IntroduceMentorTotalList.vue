<template>
  <div class="introduceMentorTotalList_wrap clearFix" v-show="active">
    <div class="inner">
      <div class="listBox" v-for="(list,idx) in totalList" :key="idx">
        <a :href="`/m/${list.url}`">
          <div class="boxInner">
            <div class="mentorImg">
              <img :src="list.profileImage" alt="멘토이미지" @error="onImageError">
            </div>
            <h1>{{ list.mentorTitle }}</h1>
          </div>
        </a>
      </div>
    </div>
    <div class="noItems" v-if="totalList.length <= 0">
      <p><i class="xi-error-o"></i>최근 등록된 멘토가 없습니다</p>
    </div>
    <p>*{{ updateTime }} 수익률 합계 순</p>
  </div>
</template>

<script>
export default {
  props: ['mentorData', 'active'],
  data() {
    return {
      totalList: [],
      updateTime: null,
    }
  },
  mounted() {
  },
  watch: {
    mentorData(val) {
      this.setTotalMentorListData(val);
    }
  },
  methods: {
    setTotalMentorListData(val) {
      this.totalList = val.totalMentorLists;
      this.updateTime = new Date(this.mentorData.updatedTime).format('yyyy년 MM월 dd일 hh시');
    },
    onImageError(e) {
      e.target.src = `${process.env.VUE_APP_IMG_URL_Mentor}default.png`;
    }
  }
}
</script>

<style lang='scss'>
  @import './css/introduceMentorTotalList.scss';
</style>