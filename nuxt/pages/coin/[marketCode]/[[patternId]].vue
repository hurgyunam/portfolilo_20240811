<template>
  <div class="coin-detailPage-wrap page-header-pt">
    <Head>
      <Title>{{ useState<CoinDto>('coinHeadTitle').value?.koreanName }}({{ useState<CoinDto>('coinHeadTitle').value?.coinCode }}) 분석 - 코싸인(COSIGN)</Title>
    </Head>
    <ClientOnly fallback-tag="p" fallback="Loading chart..." class="text-center text-14 py-32 bg-white">
      <CoinTvChart class="mb-primary" :marketCode="marketCode" :patternId="patternId"/>
    </ClientOnly>
    <div class="coin-analysis-wrap bg-white pb-second">
      <ul class="tab-nav relative f-items-justify-c justify-between border-b-1-gray2 overflow-hidden">
        <li
          v-for="nav,idx in coinAnalysisNavList" :key="nav.id"
          ref="tabButtons"
          @click="onClickTab(idx)"
          :class="{'active': tabContentUI.tabIndex === idx}"
          class="flex-1 py-second text-center cursor-pointer" 
        >
          <div 
            v-if="!nav.icon" 
            class="text text-14" 
            :class="{'text-mint font-medium': tabContentUI.tabIndex === idx}"
          >{{ nav.name }}</div>
          <div
            v-else 
            class="icon-wrap w-10 mx-auto"
            :class="tabContentUI.tabIndex === idx ? 'filter-none' : 'grayscale-1'"
          >
            <NuxtImg
              :src="nav.icon" 
              :alt="nav.name"
              format="webp" 
              loading="lazy"
            />
          </div>
        </li>
        <span ref="navBar" class="nav-bar absolute block bottom-0 left-0 w-20 h-0.5 bg-mint transition-transform-second" :style="navBarStyle"></span>
      </ul>

      <div class="tab-content">
        <Swiper class="coinAnalysSwiper"
          :speed="300"
          :threshold="1"
          :noSwiping="true"
          :noSwipingClass="'no-swiping'"
          :auto-height="true"
          @init="swiperInit"
          @slideChange="slideChange"
        >
          <SwiperSlide :class="{'no-swiping': noSwipe}">
            <!-- <CoinAnalysisChart @noSwiper="noSwiper" /> -->
            <CoinAnalysisPatterns :noSwiper="noSwiper" />
          </SwiperSlide>
          <SwiperSlide>
            <CoinAnalysisCoupling />
          </SwiperSlide>
          <SwiperSlide>
            <CoinAnalysisSignList 
              :marketCode="marketCode"
            />
          </SwiperSlide>
        </Swiper>
      </div>

    </div>
  </div>
</template>
  
<script setup lang="ts">
import type Swiper from 'swiper/types/swiper-class.d.ts';
import { useCoinWidget } from '@/pinia/coin/widget';
import { useTabContentUI } from '~~/pinia/coin/tabcontentui';
import { useCoinHeader, CoinDto } from '~~/composables/coin/header';
import { onBeforeRouteLeave } from 'vue-router';
  
const { params } = useRoute();
const marketCode = params.marketCode as string;
const patternId = params.patternId as string;

const { setActive, controller } = useCoinWidget();
const tabButtons = ref<HTMLElement[]>([]);

// 주석처리
// const tabTransformWrap = ref<HTMLElement>(null);

const coinAnalysisNavList = [    
{
      name: '차트 분석',
      id: 'up'
    },
    {
      name: '커플링',
      id: 'down'
    },
    {
      name: '싸인리스트',
      icon: 'https://kr.object.ncloudstorage.com/cosign/version1/aimodel/sign.png',
      id: 'amount'
    },
]

const data = await useCoinHeader(marketCode);
useState<CoinDto | null>('coinHeadTitle').value = data;


onBeforeRouteLeave(() => {
  useState<CoinDto | null>('coinHeadTitle').value = null;
})


// const coinAnalysisNav = reactive<CoinAnalysisNav>({
//   coinAnalysisIdx: 0
// })

const navBar = ref<HTMLElement | null>(null);

const navBarLeftCss = reactive({
  width: 0,
  transform: 0
});
const navBarStyle = computed(() => {
  return {
    width: `${navBarLeftCss.width}px`,
    transform: `translate3d(${navBarLeftCss.transform}px,0,0)`
  }
})


const { init, updateElementHeight, tabContentUI, loadSigns, loadCoupling } = useTabContentUI();


/** swiper */
let coinAnalysisSwiper:Swiper | null = null;
let coinAnalysisSwiperWrapper:HTMLElement | null = null;
const noSwipe = ref<boolean>(false);

function noSwiper(value:boolean) {
  noSwipe.value = value;
}


function swiperInit(sw:Swiper) {
  sw.wrapperEl.style.transitionProperty = 'transform, height';
  coinAnalysisSwiper = sw;
  coinAnalysisSwiperWrapper = sw.wrapperEl;
}

function slideChange(sw:Swiper) {
  tabContentUI.tabIndex = sw.activeIndex;
  setLoadContent(tabContentUI.tabIndex);
}


// ===========================
// 커플링 분석 데이터 api
// ===========================

async function setLoadContent(index:number) {
  // console.log("setLoadContent", index, tabButtons.value.length);
  if (tabButtons.value.length > 0) {
    tabContentUI.tabIndex = index;

    const targetTag = tabButtons.value[index];

    navBarLeftCss.transform = targetTag.offsetLeft;

    if (controller.widget) {
      controller.widget.onChartReady(() => {
        if (index === 0) {
          setActive(true);
        } else {
          setActive(false);
        }
      }) 
    }
    
    if (index === 1) { // 커플링
      await loadCoupling(marketCode);
    } else if (index === 2) {
      await loadSigns(marketCode);
    }

    await nextTick();
    updateElementHeight();

    const router = useRouter();
    switch(index) {
      case 0:
        router.replace(`${location.pathname}?tab=analysis`);
        break;
      case 1:
        router.replace(`${location.pathname}?tab=coupling`);
        break;
      case 2:
        router.replace(`${location.pathname}?tab=sign`);
        break;
    }
  }
}

function onClickTab( index:number ) {
  // tabContentUI.tabIndex = index;

  coinAnalysisSwiper?.slideTo(index, 300);
  
  // setLoadContent(tabContentUI.tabIndex);
}



function setTabElementUI(actInx:number) {
  const targetTag = tabButtons.value[actInx];

  navBarLeftCss.width = targetTag.offsetWidth;
  navBarLeftCss.transform = targetTag.offsetLeft;

  tabContentUI.tabIndex = actInx;
}


// let patternPageParamsWatch:WatchStopHandle = null;
let tabIndex = 0;
const { query } = useRoute(); 
const tabQuery: string = query.tab as string;

if (tabQuery?.includes("coupling")) {
  tabIndex = 1;
} else if (tabQuery?.includes("sign")) {
  tabIndex = 2;
}

onMounted(() => {
  // console.log("mounted");    

  // window.scrollTo(0, 0); // router 이동시 기존 스크롤을 유지하려는 경향이 있어 강제로 위로 올려준다

  nextTick(() => { 
    setTabElementUI(tabIndex);
    if (coinAnalysisSwiperWrapper) init(coinAnalysisSwiperWrapper);

    // coinAnalysisSwiper.slideTo(index, 300);
    
    // if (index === 0) setLoadContent(index);

    if (tabIndex === 0) {
      setLoadContent(tabIndex);
      // updateElementHeight();
      // setActive(true);
    } else {
      coinAnalysisSwiper?.slideTo(tabIndex, 300);
    }
  })
})


</script>
