package de.felixschick.quizy.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ActivityRequest {
    private String activityType;
    private String activityLabel;
}