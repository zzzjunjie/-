package cn.sinjinsong.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.channels.SocketChannel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private String username;
    private String password;
    private SocketChannel channel;
}
