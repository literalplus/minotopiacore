package io.github.xxyy.mtc.hook;

import com.google.common.base.Preconditions;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Proxy call interceptor for Vault hook, providing additional safety layers.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-11-03
 */
public class SimpleMTCHookProxy<T extends MTCHook> implements InvocationHandler {
    private final Plugin plugin;
    private boolean available;
    private T proxied;

    public SimpleMTCHookProxy(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!available && !method.getDeclaringClass().equals(MTCHook.class)) { //Check hook availability
            available = proxied.isAvailable(plugin);
            Preconditions.checkState(available, "hook is not available!"); //Hooks can't operate when not available
        }

        return method.invoke(proxied, args);
    }

    @SuppressWarnings("unchecked")
    public T wrap(T t, Class<?>... interfaces) {
        proxied = (T) Proxy.newProxyInstance(t.getClass().getClassLoader(), interfaces, this);
        return proxied;
    }
}
