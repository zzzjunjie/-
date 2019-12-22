package cn.sinjinsong.common.domain;

import cn.sinjinsong.common.enumeration.TaskType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.channels.SocketChannel;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    private SocketChannel receiver;
    private TaskType type;
    private String desc;
    private Message message;
}
