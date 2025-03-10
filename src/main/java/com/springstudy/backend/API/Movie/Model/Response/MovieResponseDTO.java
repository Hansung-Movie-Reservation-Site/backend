package com.springstudy.backend.API.Movie.Model.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponseDTO {
    private String title;      // ✅ 영화명
    private String posterUrl;  // ✅ 포스터 이미지 링크

    /**
     * ✅ 영화명과 포스터 URL이 같으면 동일 객체로 취급 (중복 제거 가능)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MovieResponseDTO that = (MovieResponseDTO) obj;
        return Objects.equals(title, that.title) &&
                Objects.equals(posterUrl, that.posterUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, posterUrl);
    }
}