package cn.sinjinsong.common.domain;

import cn.sinjinsong.common.enumeration.ResponseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseHeader {
    private String sender;
    private ResponseType type;
    private Integer responseCode;
    private Long timestamp;
}
