package org.helfoome.presentation.restaurant.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.helfoome.domain.entity.BlogReviewInfo
import org.helfoome.domain.entity.ReviewInfo
import javax.inject.Inject

@HiltViewModel
class RestaurantReviewViewModel @Inject constructor() : ViewModel() {
    private val _reviews = MutableLiveData<List<ReviewInfo>>()
    val reviews: LiveData<List<ReviewInfo>> = _reviews
    private val _blogReviews = MutableLiveData<List<BlogReviewInfo>>()
    val blogReviews: LiveData<List<BlogReviewInfo>> = _blogReviews

    init {
        fetchReviewList()
    }

    fun fetchReviewList() {
        _reviews.value = listOf(
            ReviewInfo(1, "나는 헬푸파미", 3.6F, listOf("맛 최고", "약속 시 부담없는", "양 조절 쉬운", "든든한"), "블라블라 만약에 이 내용이 너무 길어진다면 ..? 그게 고민...이었는데 말이죠 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 제 뻘소리를 더 보고 싶으시다면 더보기를 함 눌러볼텨?", listOf("https://postfiles.pstatic.net/MjAyMjA2MTlfMTM2/MDAxNjU1NjQyOTQ5OTIw.JPYcSZuY_WIVFxj0p7XinmRXH8hEoTqfWHr8ezEifagg.L0P1lCMWpKBhUG-pDQ6SeW1cFVNxhxSJLWMaWzKeOlYg.JPEG.schneesun85/SE-a70a7e59-03f9-453a-8cc3-cb286c1b7038.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjM5/MDAxNjU1NjQyOTUzMTYy.Sw3GwqLk5rzozr4vpNwc9gRegOFw3MX7pf74jB6R1L8g.OxRoss8qTBqLK4LVKORR_eYEGGxxmym78jnrKvoNsOMg.JPEG.schneesun85/SE-a7df6cbf-07c1-49e8-87c9-2ba2cb75353c.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMTY4/MDAxNjU1NjQzMzAxMjg2.-spg_Y_enobTUhKDfpQWc3tl2ecJIf5ZapnFRqHPDeIg.K3depTagAqaC2F-NEbIcXe27Lvpuh5OaRLb-l-fR3Mwg.JPEG.schneesun85/SE-4bd590a3-3240-4226-972f-70fa3898439a.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjc2/MDAxNjU1NjQzNTEwODYx.l4nyYL53pF3-kG_HoO9Q2tZNUc6PeH6wPFZHlqhz_GYg.t4CLM6zcFFdsKvg0-FJegAsWSE4oQ0dHRIX4qEYWNdAg.JPEG.schneesun85/SE-14abe25d-a852-4915-86c1-fd0f3fa6a37b.jpg?type=w773")),
            ReviewInfo(2, "나는 헬푸파미", 3.6F, listOf("맛 최고", "약속 시 부담없는", "양 조절 쉬운", "든든한"), "블라블라 만약에 이 내용이 너무 길어진다면 ..? 그게 고민...이었는데 말이죠 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 제 뻘소리를 더 보고 싶으시다면 더보기를 함 눌러볼텨?", listOf("https://postfiles.pstatic.net/MjAyMjA2MTlfMTM2/MDAxNjU1NjQyOTQ5OTIw.JPYcSZuY_WIVFxj0p7XinmRXH8hEoTqfWHr8ezEifagg.L0P1lCMWpKBhUG-pDQ6SeW1cFVNxhxSJLWMaWzKeOlYg.JPEG.schneesun85/SE-a70a7e59-03f9-453a-8cc3-cb286c1b7038.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjM5/MDAxNjU1NjQyOTUzMTYy.Sw3GwqLk5rzozr4vpNwc9gRegOFw3MX7pf74jB6R1L8g.OxRoss8qTBqLK4LVKORR_eYEGGxxmym78jnrKvoNsOMg.JPEG.schneesun85/SE-a7df6cbf-07c1-49e8-87c9-2ba2cb75353c.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMTY4/MDAxNjU1NjQzMzAxMjg2.-spg_Y_enobTUhKDfpQWc3tl2ecJIf5ZapnFRqHPDeIg.K3depTagAqaC2F-NEbIcXe27Lvpuh5OaRLb-l-fR3Mwg.JPEG.schneesun85/SE-4bd590a3-3240-4226-972f-70fa3898439a.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjc2/MDAxNjU1NjQzNTEwODYx.l4nyYL53pF3-kG_HoO9Q2tZNUc6PeH6wPFZHlqhz_GYg.t4CLM6zcFFdsKvg0-FJegAsWSE4oQ0dHRIX4qEYWNdAg.JPEG.schneesun85/SE-14abe25d-a852-4915-86c1-fd0f3fa6a37b.jpg?type=w773")),
            ReviewInfo(3, "나는 헬푸파미", 3.6F, listOf("맛 최고", "약속 시 부담없는", "양 조절 쉬운", "든든한"), "블라블라 만약에 이 내용이 너무 길어진다면 ..? 그게 고민...이었는데 말이죠 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 제 뻘소리를 더 보고 싶으시다면 더보기를 함 눌러볼텨?", listOf("https://postfiles.pstatic.net/MjAyMjA2MTlfMTM2/MDAxNjU1NjQyOTQ5OTIw.JPYcSZuY_WIVFxj0p7XinmRXH8hEoTqfWHr8ezEifagg.L0P1lCMWpKBhUG-pDQ6SeW1cFVNxhxSJLWMaWzKeOlYg.JPEG.schneesun85/SE-a70a7e59-03f9-453a-8cc3-cb286c1b7038.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjM5/MDAxNjU1NjQyOTUzMTYy.Sw3GwqLk5rzozr4vpNwc9gRegOFw3MX7pf74jB6R1L8g.OxRoss8qTBqLK4LVKORR_eYEGGxxmym78jnrKvoNsOMg.JPEG.schneesun85/SE-a7df6cbf-07c1-49e8-87c9-2ba2cb75353c.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMTY4/MDAxNjU1NjQzMzAxMjg2.-spg_Y_enobTUhKDfpQWc3tl2ecJIf5ZapnFRqHPDeIg.K3depTagAqaC2F-NEbIcXe27Lvpuh5OaRLb-l-fR3Mwg.JPEG.schneesun85/SE-4bd590a3-3240-4226-972f-70fa3898439a.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjc2/MDAxNjU1NjQzNTEwODYx.l4nyYL53pF3-kG_HoO9Q2tZNUc6PeH6wPFZHlqhz_GYg.t4CLM6zcFFdsKvg0-FJegAsWSE4oQ0dHRIX4qEYWNdAg.JPEG.schneesun85/SE-14abe25d-a852-4915-86c1-fd0f3fa6a37b.jpg?type=w773")),
            ReviewInfo(4, "나는 헬푸파미", 3.6F, listOf("맛 최고", "약속 시 부담없는", "양 조절 쉬운", "든든한"), "블라블라 만약에 이 내용이 너무 길어진다면 ..? 그게 고민...이었는데 말이죠 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 제 뻘소리를 더 보고 싶으시다면 더보기를 함 눌러볼텨?", listOf("https://postfiles.pstatic.net/MjAyMjA2MTlfMTM2/MDAxNjU1NjQyOTQ5OTIw.JPYcSZuY_WIVFxj0p7XinmRXH8hEoTqfWHr8ezEifagg.L0P1lCMWpKBhUG-pDQ6SeW1cFVNxhxSJLWMaWzKeOlYg.JPEG.schneesun85/SE-a70a7e59-03f9-453a-8cc3-cb286c1b7038.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjM5/MDAxNjU1NjQyOTUzMTYy.Sw3GwqLk5rzozr4vpNwc9gRegOFw3MX7pf74jB6R1L8g.OxRoss8qTBqLK4LVKORR_eYEGGxxmym78jnrKvoNsOMg.JPEG.schneesun85/SE-a7df6cbf-07c1-49e8-87c9-2ba2cb75353c.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMTY4/MDAxNjU1NjQzMzAxMjg2.-spg_Y_enobTUhKDfpQWc3tl2ecJIf5ZapnFRqHPDeIg.K3depTagAqaC2F-NEbIcXe27Lvpuh5OaRLb-l-fR3Mwg.JPEG.schneesun85/SE-4bd590a3-3240-4226-972f-70fa3898439a.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjc2/MDAxNjU1NjQzNTEwODYx.l4nyYL53pF3-kG_HoO9Q2tZNUc6PeH6wPFZHlqhz_GYg.t4CLM6zcFFdsKvg0-FJegAsWSE4oQ0dHRIX4qEYWNdAg.JPEG.schneesun85/SE-14abe25d-a852-4915-86c1-fd0f3fa6a37b.jpg?type=w773")),
            ReviewInfo(5, "나는 헬푸파미", 3.6F, listOf("맛 최고", "약속 시 부담없는", "양 조절 쉬운", "든든한"), "블라블라 만약에 이 내용이 너무 길어진다면 ..? 그게 고민...이었는데 말이죠 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 제 뻘소리를 더 보고 싶으시다면 더보기를 함 눌러볼텨?", listOf("https://postfiles.pstatic.net/MjAyMjA2MTlfMTM2/MDAxNjU1NjQyOTQ5OTIw.JPYcSZuY_WIVFxj0p7XinmRXH8hEoTqfWHr8ezEifagg.L0P1lCMWpKBhUG-pDQ6SeW1cFVNxhxSJLWMaWzKeOlYg.JPEG.schneesun85/SE-a70a7e59-03f9-453a-8cc3-cb286c1b7038.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjM5/MDAxNjU1NjQyOTUzMTYy.Sw3GwqLk5rzozr4vpNwc9gRegOFw3MX7pf74jB6R1L8g.OxRoss8qTBqLK4LVKORR_eYEGGxxmym78jnrKvoNsOMg.JPEG.schneesun85/SE-a7df6cbf-07c1-49e8-87c9-2ba2cb75353c.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMTY4/MDAxNjU1NjQzMzAxMjg2.-spg_Y_enobTUhKDfpQWc3tl2ecJIf5ZapnFRqHPDeIg.K3depTagAqaC2F-NEbIcXe27Lvpuh5OaRLb-l-fR3Mwg.JPEG.schneesun85/SE-4bd590a3-3240-4226-972f-70fa3898439a.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjc2/MDAxNjU1NjQzNTEwODYx.l4nyYL53pF3-kG_HoO9Q2tZNUc6PeH6wPFZHlqhz_GYg.t4CLM6zcFFdsKvg0-FJegAsWSE4oQ0dHRIX4qEYWNdAg.JPEG.schneesun85/SE-14abe25d-a852-4915-86c1-fd0f3fa6a37b.jpg?type=w773")),
            ReviewInfo(6, "나는 헬푸파미", 3.6F, listOf("맛 최고", "약속 시 부담없는", "양 조절 쉬운", "든든한"), "블라블라 만약에 이 내용이 너무 길어진다면 ..? 그게 고민...이었는데 말이죠 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 제 뻘소리를 더 보고 싶으시다면 더보기를 함 눌러볼텨?", listOf("https://postfiles.pstatic.net/MjAyMjA2MTlfMTM2/MDAxNjU1NjQyOTQ5OTIw.JPYcSZuY_WIVFxj0p7XinmRXH8hEoTqfWHr8ezEifagg.L0P1lCMWpKBhUG-pDQ6SeW1cFVNxhxSJLWMaWzKeOlYg.JPEG.schneesun85/SE-a70a7e59-03f9-453a-8cc3-cb286c1b7038.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjM5/MDAxNjU1NjQyOTUzMTYy.Sw3GwqLk5rzozr4vpNwc9gRegOFw3MX7pf74jB6R1L8g.OxRoss8qTBqLK4LVKORR_eYEGGxxmym78jnrKvoNsOMg.JPEG.schneesun85/SE-a7df6cbf-07c1-49e8-87c9-2ba2cb75353c.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMTY4/MDAxNjU1NjQzMzAxMjg2.-spg_Y_enobTUhKDfpQWc3tl2ecJIf5ZapnFRqHPDeIg.K3depTagAqaC2F-NEbIcXe27Lvpuh5OaRLb-l-fR3Mwg.JPEG.schneesun85/SE-4bd590a3-3240-4226-972f-70fa3898439a.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjc2/MDAxNjU1NjQzNTEwODYx.l4nyYL53pF3-kG_HoO9Q2tZNUc6PeH6wPFZHlqhz_GYg.t4CLM6zcFFdsKvg0-FJegAsWSE4oQ0dHRIX4qEYWNdAg.JPEG.schneesun85/SE-14abe25d-a852-4915-86c1-fd0f3fa6a37b.jpg?type=w773")),
            ReviewInfo(7, "나는 헬푸파미", 3.6F, listOf("맛 최고", "약속 시 부담없는", "양 조절 쉬운", "든든한"), "블라블라 만약에 이 내용이 너무 길어진다면 ..? 그게 고민...이었는데 말이죠 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 제 뻘소리를 더 보고 싶으시다면 더보기를 함 눌러볼텨?", listOf("https://postfiles.pstatic.net/MjAyMjA2MTlfMTM2/MDAxNjU1NjQyOTQ5OTIw.JPYcSZuY_WIVFxj0p7XinmRXH8hEoTqfWHr8ezEifagg.L0P1lCMWpKBhUG-pDQ6SeW1cFVNxhxSJLWMaWzKeOlYg.JPEG.schneesun85/SE-a70a7e59-03f9-453a-8cc3-cb286c1b7038.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjM5/MDAxNjU1NjQyOTUzMTYy.Sw3GwqLk5rzozr4vpNwc9gRegOFw3MX7pf74jB6R1L8g.OxRoss8qTBqLK4LVKORR_eYEGGxxmym78jnrKvoNsOMg.JPEG.schneesun85/SE-a7df6cbf-07c1-49e8-87c9-2ba2cb75353c.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMTY4/MDAxNjU1NjQzMzAxMjg2.-spg_Y_enobTUhKDfpQWc3tl2ecJIf5ZapnFRqHPDeIg.K3depTagAqaC2F-NEbIcXe27Lvpuh5OaRLb-l-fR3Mwg.JPEG.schneesun85/SE-4bd590a3-3240-4226-972f-70fa3898439a.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjc2/MDAxNjU1NjQzNTEwODYx.l4nyYL53pF3-kG_HoO9Q2tZNUc6PeH6wPFZHlqhz_GYg.t4CLM6zcFFdsKvg0-FJegAsWSE4oQ0dHRIX4qEYWNdAg.JPEG.schneesun85/SE-14abe25d-a852-4915-86c1-fd0f3fa6a37b.jpg?type=w773")),
            ReviewInfo(8, "나는 헬푸파미", 3.6F, listOf("맛 최고", "약속 시 부담없는", "양 조절 쉬운", "든든한"), "블라블라 만약에 이 내용이 너무 길어진다면 ..? 그게 고민...이었는데 말이죠 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 제 뻘소리를 더 보고 싶으시다면 더보기를 함 눌러볼텨?", listOf("https://postfiles.pstatic.net/MjAyMjA2MTlfMTM2/MDAxNjU1NjQyOTQ5OTIw.JPYcSZuY_WIVFxj0p7XinmRXH8hEoTqfWHr8ezEifagg.L0P1lCMWpKBhUG-pDQ6SeW1cFVNxhxSJLWMaWzKeOlYg.JPEG.schneesun85/SE-a70a7e59-03f9-453a-8cc3-cb286c1b7038.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjM5/MDAxNjU1NjQyOTUzMTYy.Sw3GwqLk5rzozr4vpNwc9gRegOFw3MX7pf74jB6R1L8g.OxRoss8qTBqLK4LVKORR_eYEGGxxmym78jnrKvoNsOMg.JPEG.schneesun85/SE-a7df6cbf-07c1-49e8-87c9-2ba2cb75353c.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMTY4/MDAxNjU1NjQzMzAxMjg2.-spg_Y_enobTUhKDfpQWc3tl2ecJIf5ZapnFRqHPDeIg.K3depTagAqaC2F-NEbIcXe27Lvpuh5OaRLb-l-fR3Mwg.JPEG.schneesun85/SE-4bd590a3-3240-4226-972f-70fa3898439a.jpg?type=w773", "https://postfiles.pstatic.net/MjAyMjA2MTlfMjc2/MDAxNjU1NjQzNTEwODYx.l4nyYL53pF3-kG_HoO9Q2tZNUc6PeH6wPFZHlqhz_GYg.t4CLM6zcFFdsKvg0-FJegAsWSE4oQ0dHRIX4qEYWNdAg.JPEG.schneesun85/SE-14abe25d-a852-4915-86c1-fd0f3fa6a37b.jpg?type=w773")),
        )
    }

    fun fetchBlogReviewList() {
        _blogReviews.value = listOf(
            BlogReviewInfo(1, "샐러디 안암점 나쵸가 씹히는 멕시칸랩", "블라블라 맛 멋져요 멍멍 만약에 이 내용이 너무 길어진다면 ..? 그게 고민..이었는데 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 더보기나 나올 텍스트 크기는 이 … ", "https://postfiles.pstatic.net/MjAyMjA2MTlfMTM2/MDAxNjU1NjQyOTQ5OTIw.JPYcSZuY_WIVFxj0p7XinmRXH8hEoTqfWHr8ezEifagg.L0P1lCMWpKBhUG-pDQ6SeW1cFVNxhxSJLWMaWzKeOlYg.JPEG.schneesun85/SE-a70a7e59-03f9-453a-8cc3-cb286c1b7038.jpg?type=w773"),
            BlogReviewInfo(2, "샐러디 안암점 나쵸가 씹히는 멕시칸랩", "블라블라 맛 멋져요 멍멍 만약에 이 내용이 너무 길어진다면 ..? 그게 고민..이었는데 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 더보기나 나올 텍스트 크기는 이 … ", "https://postfiles.pstatic.net/MjAyMjA2MTlfMTM2/MDAxNjU1NjQyOTQ5OTIw.JPYcSZuY_WIVFxj0p7XinmRXH8hEoTqfWHr8ezEifagg.L0P1lCMWpKBhUG-pDQ6SeW1cFVNxhxSJLWMaWzKeOlYg.JPEG.schneesun85/SE-a70a7e59-03f9-453a-8cc3-cb286c1b7038.jpg?type=w773"),
            BlogReviewInfo(3, "샐러디 안암점 나쵸가 씹히는 멕시칸랩", "블라블라 맛 멋져요 멍멍 만약에 이 내용이 너무 길어진다면 ..? 그게 고민..이었는데 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 더보기나 나올 텍스트 크기는 이 … ", "https://postfiles.pstatic.net/MjAyMjA2MTlfMTM2/MDAxNjU1NjQyOTQ5OTIw.JPYcSZuY_WIVFxj0p7XinmRXH8hEoTqfWHr8ezEifagg.L0P1lCMWpKBhUG-pDQ6SeW1cFVNxhxSJLWMaWzKeOlYg.JPEG.schneesun85/SE-a70a7e59-03f9-453a-8cc3-cb286c1b7038.jpg?type=w773"),
            BlogReviewInfo(4, "샐러디 안암점 나쵸가 씹히는 멕시칸랩", "블라블라 맛 멋져요 멍멍 만약에 이 내용이 너무 길어진다면 ..? 그게 고민..이었는데 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 더보기나 나올 텍스트 크기는 이 … ", "https://postfiles.pstatic.net/MjAyMjA2MTlfMTM2/MDAxNjU1NjQyOTQ5OTIw.JPYcSZuY_WIVFxj0p7XinmRXH8hEoTqfWHr8ezEifagg.L0P1lCMWpKBhUG-pDQ6SeW1cFVNxhxSJLWMaWzKeOlYg.JPEG.schneesun85/SE-a70a7e59-03f9-453a-8cc3-cb286c1b7038.jpg?type=w773"),
            BlogReviewInfo(5, "샐러디 안암점 나쵸가 씹히는 멕시칸랩", "블라블라 맛 멋져요 멍멍 만약에 이 내용이 너무 길어진다면 ..? 그게 고민..이었는데 해결됐어요. 왜냐면 더보기를 누르면 되니까요! 더보기나 나올 텍스트 크기는 이 … ", "https://postfiles.pstatic.net/MjAyMjA2MTlfMTM2/MDAxNjU1NjQyOTQ5OTIw.JPYcSZuY_WIVFxj0p7XinmRXH8hEoTqfWHr8ezEifagg.L0P1lCMWpKBhUG-pDQ6SeW1cFVNxhxSJLWMaWzKeOlYg.JPEG.schneesun85/SE-a70a7e59-03f9-453a-8cc3-cb286c1b7038.jpg?type=w773")
        )
    }
}
