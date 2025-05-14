package az.qrfood.backend.common;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import java.lang.reflect.InvocationTargetException;

@Log4j2
public class Util {

    public static <SR, DS> DS copyProperties(SR source, Class<DS> destinationClass) {
        DS target = null;
        try {
            target = destinationClass.getDeclaredConstructor().newInstance();
            // use instance
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("Error instantiating " + destinationClass, e);
        }
        assert target != null;
        BeanUtils.copyProperties(source, target);
        return target;
    }
}