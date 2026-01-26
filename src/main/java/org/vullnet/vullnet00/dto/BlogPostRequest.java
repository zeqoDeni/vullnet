package org.vullnet.vullnet00.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BlogPostRequest {
    @NotBlank
    private String title;
    private String summary;
    @NotBlank
    private String content;
    private String coverImageUrl;
    private Boolean published = true;
    @JsonSetter(nulls = Nulls.AS_EMPTY, contentNulls = Nulls.SKIP)
    private List<String> gallery = new ArrayList<>();
}
