import cn.hutool.json.JSONUtil;
import com.lms.init.model.entity.User;
import com.lms.init.model.vo.UserVo;
import org.junit.jupiter.api.Test;

import static com.lms.init.model.factory.UserFactory.USER_CONVERTER;

public class MapStructTest {

    @Test
    public void test(){
        User user=new User();
        user.setId(20L);
        user.setUsername("zdh");
        user.setNickname("zdh");
        UserVo userVo = USER_CONVERTER.toUserVo(user);
        System.out.println(JSONUtil.toJsonStr(userVo));
    }
}
