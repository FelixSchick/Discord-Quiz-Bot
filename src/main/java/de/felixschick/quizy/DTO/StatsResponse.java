package de.felixschick.quizy.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
public class StatsResponse {
    private int serverCount;
    private boolean mySQLConnection;
}
