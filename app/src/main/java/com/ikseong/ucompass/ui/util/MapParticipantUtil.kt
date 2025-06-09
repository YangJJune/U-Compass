package com.ikseong.ucompass.ui.util

import com.ikseong.ucompass.ui.model.Direction
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

object MapParticipantUtil {

    // 두 위·경도 좌표 사이의 진북 기준 방위각(bearing)을 계산
    private fun calculateBearing(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)
        val lon1Rad = Math.toRadians(lon1)
        val lon2Rad = Math.toRadians(lon2)
        val deltaLon = lon2Rad - lon1Rad

        val y = sin(deltaLon) * cos(lat2Rad)
        val x = cos(lat1Rad) * sin(lat2Rad) - sin(lat1Rad) * cos(lat2Rad) * cos(deltaLon)
        var bearingRad = atan2(y, x)
        val bearingDeg = (Math.toDegrees(bearingRad) + 360.0) % 360.0
        return bearingDeg
    }

    // 2) 두 진북 기준 방위각 사이의 상대 회전 각도를 –180~+180 범위로 계산
    private fun calcRelativeAngle(
        bearingFrom: Double,
        bearingTo: Double
    ): Double {
        var diff = (bearingTo - bearingFrom + 360.0) % 360.0
        if (diff > 180.0) {
            diff -= 360.0
        }
        return diff
    }

    // 3) “내 위도·경도”와 “내 현재 방위각(진북 기준)”, “목표 위도·경도”를 입력받아
    // → 목표 지점이 내 기준으로부터 시계방향(우), 반시계방향(좌)으로 얼마나 떨어져 있는지(d°) 계산
    fun getRelativeBearing(
        myLat: Double,
        myLon: Double,
        myHeading: Double,
        targetLat: Double,
        targetLon: Double
    ): Double {
        val targetBearing = calculateBearing(myLat, myLon, targetLat, targetLon)
        return calcRelativeAngle(myHeading, targetBearing)
    }

    /**
     *  4) “내 방향”을 기준으로 상대방이 대략 어느 사분면(앞=‘N’, 오른쪽=‘E’, 뒤=‘S’, 왼쪽=‘W’)에 있는지 판별하여 문자열로 반환
     *
     * @param relativeAngle  getRelativeBearing(...)을 통해 얻은 상대 회전 각도 (–180° ≤ … ≤ +180°)
     * @return “N” (목표가 내 앞),
     *         “E” (목표가 내 오른쪽),
     *         “S” (목표가 내 뒤),
     *         “W” (목표가 내 왼쪽)
     */
    fun getCardinalDirectionFromRelative(relativeAngle: Double): Direction {
        return when {
            relativeAngle >= -45.0 && relativeAngle <= 45.0 -> Direction.N
            relativeAngle > 45.0 && relativeAngle <= 135.0 -> Direction.E
            relativeAngle < -45.0 && relativeAngle >= -135.0 -> Direction.W
            else -> Direction.S  // 남쪽: 상대각이 (135,180] 또는 (-180,-135) 구간
        }
    }

    /**
     * 직사각형(가로 W, 세로 H)을 기준으로,
     * “내가 정가운데(0,0)에 있고 정면(진북, +Y축)을 바라볼 때”
     * 주어진 진북 기준 방위각(bearingDeg) 방향으로 뻗은 직선이 닿는
     * 직사각형의 면(“N”, “E”, “S”, “W”)과
     * 그 면의 중점으로부터의 수직 거리를 함께 반환합니다.
     *
     * @param W           직사각형의 전체 가로 길이 (>0)
     * @param H           직사각형의 전체 세로 길이 (>0)
     * @param bearingDeg  진북 기준 방위각 (단위: 도, 0°=북, 90°=동, 180°=남, 270°=서)
     * @return Triple(핀 방향 Direction, Align 기준 Direction, 거리: 면 중점까지의 거리)
     */
    fun rectangleSideAndDistance(
        W: Double,
        H: Double,
        bearingDeg: Double
    ): Triple<Direction, Direction, Double> {
        require(W > 0 && H > 0)

        val halfW = W / 2.0
        val halfH = H / 2.0

        // ① 방위각을 0°~360°로 정규화하고 라디안으로 변환
        val angle = ((bearingDeg % 360.0) + 360.0) % 360.0
        val θ = Math.toRadians(angle)

        // ② 방향 벡터 (dx, dy)
        val dx = sin(θ)   // 동쪽(+x) 방향 성분
        val dy = cos(θ)   // 북쪽(+y) 방향 성분

        // ③ 네 면과 만나는 t 후보들 계산 (양수만 의미)
        //   - 위쪽 면(y = +halfH)과 교차하면 tTop = (halfH) / dy      (dy>0)
        //   - 아래쪽 면(y = -halfH)과 교차하면 tBottom = (-halfH) / dy (dy<0)
        //   - 오른쪽 면(x = +halfW)과 교차하면 tRight = (halfW) / dx   (dx>0)
        //   - 왼쪽 면(x = -halfW)과 교차하면 tLeft = (-halfW) / dx     (dx<0)
        val tTop = if (dy > 0) halfH / dy else Double.POSITIVE_INFINITY
        val tBottom = if (dy < 0) -halfH / dy else Double.POSITIVE_INFINITY
        val tRight = if (dx > 0) halfW / dx else Double.POSITIVE_INFINITY
        val tLeft = if (dx < 0) -halfW / dx else Double.POSITIVE_INFINITY

        // ④ 유효한(>0) t 중 가장 작은 값을 골라 그 면을 선택
        //    (예: 가장 먼저 만나는 경계점이 어디인지 결정)
        val tCandidates = listOf(
            tTop to Direction.N,
            tRight to Direction.E,
            tBottom to Direction.S,
            tLeft to Direction.W
        ).filter { it.first > 0 }  // 양수인 것만 남김
        val (tMin, side) = tCandidates.minByOrNull { it.first }
            ?: throw IllegalArgumentException("유효한 교차점이 없습니다. bearingDeg=$bearingDeg, W=$W, H=$H")

        // ⑤ 교차점 좌표 (ix, iy)
        val ix = tMin * dx
        val iy = tMin * -dy

        // ⑥ 닿은 면이 N/S 면이면 “면 중점 y=±halfH”까지 수평 거리 = |ix|
        //     닿은 면이 E/W 면이면 “면 중점 x=±halfW”까지 수직 거리 = |iy|
        val distance = when (side) {
            Direction.N, Direction.S -> (ix)
            else -> (iy)
        }

        val finalSide = when (side) {
            Direction.N -> when {
                ix > halfW / 2 -> Direction.NE
                ix < -(halfW / 2) -> Direction.NW
                else -> Direction.N
            }

            Direction.E -> when {
                iy > halfH / 2 -> Direction.SE
                iy < -(halfH / 2) -> Direction.NE
                else -> Direction.E
            }

            Direction.S -> when {
                ix > halfW / 2 -> Direction.SE
                ix < -(halfW / 2) -> Direction.SW
                else -> Direction.S
            }

            Direction.W -> when {
                iy > halfH / 2 -> Direction.SW
                iy < -(halfH / 2) -> Direction.NW
                else -> Direction.W
            }

            else -> throw IllegalArgumentException("Invalid side: $side")
        }
//        Log.d(
//            "MapParticipantUtil",
//            "rectangleSideAndDistance: iy = $iy, halfH = $halfH, side=$finalSide, distance=$distance"
//        )
//        Log.d(
//            "MapParticipantUtil",
//            "rectangleSideAndDistance: ix = $ix, halfH = $halfW, side=$finalSide, distance=$distance"
//        )

        return Triple(finalSide, side, distance)
    }
}