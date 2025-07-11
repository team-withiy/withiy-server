package com.server.domain.badge.entity;

public enum BadgeType {

    WITHIY_COUPLE("위디커플", "커플 연결을 하면 받을 수 있어요!", "하트 머리핀 or 머리띠"),
    WITHIY_MEMORY("위디추억", "첫 추억을 위디와 함께했어요~", "폴라로이드 사진 배경 or 어깨에 작은 카메라"),
    WITHIY_REGULAR("위디단골", "일주일 내내 위디에 들렀어요. (🙋‍♀️ 한 명만 매일 들러도 배지가 지급돼요!)", "리본 목도리"),
    WITHIY_HOLIC("위디홀릭", "한 달 동안 매일 위디에 접속했어요. (🫶 두 사람 모두 매일 접속해야 받아요!)", "반짝이는 왕관 or 별빛 후광"),
    PLACE_LOVER("장소러버", "장소 5곳 이상 저장했어요. (🙋‍♀️ 한 사람 기준으로 지급돼요)", "지도 무늬 가방 or 발 아래 발자국 아이콘"),
    COURSE_FAIRY("코스요정", "우리만의 데이트 코스를 직접 만들었어요. (🙋‍♂️ 한 사람만 코스를 만들어도 받을 수 있어요)", "마법봉 or 요정 날개"),
    ANNIVERSARY_FAIRY("기념요정", "기념일 기록을 위디에 남겼어요! (🙋‍♀️ 한 사람이 기록해도 지급돼요)", "기념일 케이크 소품"),
    MEMORY_MASTER("추억장인", "10번 넘게 추억 남긴 사랑꾼 인증💘 (🫶 두 사람 합산 기준이에요)", "앨범 소품"),
    WITHIY_EXPLORER("위디탐험가", "벌써 20개의 추억이라니! (🫶 두 사람 합산 기준이에요)", "모험가 모자"),
    FOOD_LOVER("맛집러버", "맛집 데이트 3회 돌파! 🍜 (🫶 두 사람 추억 합산 기준이에요)", "포크"),
    SHOPPING_LOVER("쇼핑러버", "쇼핑 3회 클리어! 🛍 (🫶 두 사람 추억 합산 기준이에요)", "선글라스 + 쇼핑백"),
    ACTIVITY_RUNNER("액티비티런", "액티비티 3회 클리어! 🏃‍♀️ (🫶 두 사람 추억 합산 기준이에요)", "운동화, 달리는 모습"),
    HEALING_LOVER("힐링러버", "힐링 데이트 3번 🌿 (🫶 두 사람 추억 합산 기준이에요)", "잎사귀 베개 or 안대 착용"),
    PERFORMANCE_LOVER("공연러버", "공연/전시 3회 관람! 🎭 (🫶 두 사람 추억 합산 기준이에요)", "티켓 꼽힌 머리띠"),
    TOGETHER_300("정들었지", "커플 연결 300일 달성! 함께한 시간, 고마워요 💑 (🫶 연결 상태 유지 기준이에요)", "‘위디와 300일’ 숫자풍선");

    private final String displayName;
    private final String description;
    private final String item;

    BadgeType(String displayName, String description, String item) {
        this.displayName = displayName;
        this.description = description;
        this.item = item;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getItem() {
        return item;
    }
}
