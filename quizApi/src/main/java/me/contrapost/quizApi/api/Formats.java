package me.contrapost.quizApi.api;

import javax.ws.rs.core.MediaType;

/**
 * Created by alexandershipunov on 07/11/2016.
 *
 */
public interface Formats {
    String JSON_V1 = MediaType.APPLICATION_JSON + "; charset=UTF-8; version=1";
    String JSON_MERGE_V1 = "application/merge-patch+json; charset=UTF-8; version=1";
    String HAL_V1 = "application/hal+json; charset=UTF-8; version=1";
}
