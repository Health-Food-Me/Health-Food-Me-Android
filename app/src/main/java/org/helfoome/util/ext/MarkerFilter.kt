package org.helfoome.util.ext

import org.helfoome.domain.entity.MarkerInfo

// 전체 마커 리스트 정보(마커인포 리스트).markerFilter(파라미터 : 새롭게 검색이나 스크랩 인포메이션을 불렀을 때 정보들에서 id값만 리스트로 뺀 값)
fun List<MarkerInfo>.markerFilter(markerInput: List<String>) = filter { markerInfo ->
    markerInput.forEach {
        if (markerInfo.id == it)
            return@filter true
    }
    return@filter false
}
