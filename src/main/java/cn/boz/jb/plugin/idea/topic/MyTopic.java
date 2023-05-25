package cn.boz.jb.plugin.idea.topic;

import com.intellij.util.messages.Topic;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class MyTopic extends Topic {

    @SuppressWarnings("unchecked")
    public MyTopic(@NonNls @NotNull String name, @NotNull Class listenerClass) {
        super(name, listenerClass);
    }
}
