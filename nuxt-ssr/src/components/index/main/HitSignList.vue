<template>
  <!-- <transition name="opacity"> -->
    <div class="hitSignList_wrap">
      <div class="hitSignList_inner" v-if="items.length>0">
        <div class="title">
          <ul>
            <!-- <img :src="`${$static.imgUrl}index/main/filterIcon.png`" alt="필터아이콘"> -->
            <li class="coin">
              <span @click="sortingItems('market')">코인이름
                <i class="xi-caret-down" :class="{on: sortFlag['market'] === true, off: sortFlag['market'] === false}"></i>
              </span>
            </li>
            <li class="earning">
              <span @click="sortingItems('earningRate')">수익률
                <i class="xi-caret-down" :class="{on: sortFlag['earningRate'] === true, off: sortFlag['earningRate'] === false}"></i>
              </span>
            </li>
            <li class="validate">
              <span @click="sortingItems('validateMin')">유효기간
                <i class="xi-caret-down" :class="{on: sortFlag['validateMin'] === true, off: sortFlag['validateMin'] === false}"></i>
              </span>
            </li>
            <li class="mentor">
              <span @click="sortingItems('mentorTitle')">멘토이름
                <i class="xi-caret-down" :class="{on: sortFlag['mentorTitle'] === true, off: sortFlag['mentorTitle'] === false}"></i>
              </span>
            </li>
          </ul>
        </div>
        <div class="predictSign_wrap">
          <div class="shadow" :class="{on: shadow}"></div>
          <div class="scroll_inner" ref="scroll_inner" @scroll="scrollSignList">
            <HitSign
              v-for="(item,idx) in items" :key="idx"
              :item="item"
            />
          </div>
        </div>
      </div>
      <div class="noItems" v-else>
        <p><i class="xi-error-o"></i>최근 등록된 싸인이 없습니다</p>
      </div>
    </div>
  <!-- </transition> -->
</template>

<script>
import HitSign from '@/src/components/index/main/HitSign'

export default {
  props: ['items'],
  components: {HitSign},
  data() {
    return {
      shadow: true,
      sortFlag: {
        market: null,
        mentorTitle: null,
        earningRate: null,
        validateMin: null,
      },
      sortBowl: [],
    }
  },
  async mounted() {
    this.init()
    // await this.loadSign();
  },
  // watch: {
  //   items(val) {
  //     if(val.length > 0) {
  //       this.$nextTick(()=> {
  //         const $scroll_inner = this.$refs.scroll_inner;
  //         if($scroll_inner.offsetHeight >= $scroll_inner.scrollHeight) this.shadow = false;
  //       })
  //     }
  //   },
  // },
  methods: {
    init() {
      if(this.items.length > 0) {
        this.$nextTick(()=> {
          const $scroll_inner = this.$refs.scroll_inner;
          if($scroll_inner.offsetHeight >= $scroll_inner.scrollHeight) this.shadow = false;
        })
      }
    },
    getMinuteText(minute) {
      if(minute / 60 < 1) {
        return `${minute}분`;
      } else {
        return `${Math.ceil(minute/60)}시간`;
      }
    },
    scrollSignList() {
      const $scroll_inner = this.$refs.scroll_inner;
      if($scroll_inner.scrollTop >= $scroll_inner.scrollHeight - $scroll_inner.offsetHeight - 2) {
        this.shadow = false;
      } else {
        this.shadow = true;
      }
    },
    initSortFlag(filter) {
      const sortFlagKeys = Object.keys(this.sortFlag)
      for(let i = 0; i < sortFlagKeys.length; i++) {
        if(sortFlagKeys[i] === filter) continue;
        this.sortFlag[sortFlagKeys[i]] = null;
      }
    },
    sortFlagToggle(filter) {
      this.initSortFlag(filter)
      if(this.sortFlag[filter]) {
        this.sortFlag[filter] = false;
      } else {
        this.sortFlag[filter] = true;
      }
    },
    sortingItems(filter) {
      this.sortFlagToggle(filter);

      if(filter === 'market' || filter === 'mentorTitle') {
        this.nameStringSorting(filter);
        return;
      }

      this.items.sort((a,b) => {
        if(this.sortFlag[filter]) return b[filter] - a[filter];
        else return a[filter] - b[filter];
      })
    },
    nameStringSorting(filter) {
      if(this.sortFlag[filter]) {
        this.items.sort((a,b)=> {
          if(a[filter] > b[filter]) return -1;
          else if(a[filter] < b[filter]) return 1;
          else return 0;
        })
      } else {
        this.items.sort((a,b)=> {
          if(a[filter] > b[filter]) return 1;
          else if(a[filter] < b[filter]) return -1;
          else return 0;
        })
      }
    }
  }
}
</script>

<style lang='scss'>
  @import './css/hitSignList.scss';
</style>