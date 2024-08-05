package de.felixschick.quizy.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityRequest {
    private String activityType;
    private String activityLabel;
}