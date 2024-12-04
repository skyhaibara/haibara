package top.haibara.haibara;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
public class HaibaraApplication extends Application {
    // 这里不需要做任何资源类注册，Jersey 会自动扫描指定的包
}
