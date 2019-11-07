package tech.tisson.sequence;

import com.google.common.collect.ImmutableList;
import org.apache.commons.collections4.list.AbstractListDecorator;

import java.net.*;
import java.util.Enumeration;
import java.util.List;

/**
 * IP地址辅助类，日后实现基于路由表找出最合适的接口？
 *
 * @author zhuzhiou
 */
public final class InetAddressCollection extends AbstractListDecorator<InetAddress> {

    private InetAddressCollection(List<InetAddress> interfaceAddresses) {
        super(interfaceAddresses);
    }

    public static List<InetAddress> getAllNonLoopInet4Address() throws SocketException {
        ImmutableList.Builder<InetAddress> list = ImmutableList.builder();
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            if (networkInterface.isLoopback()) {
                continue;
            }
            if (!networkInterface.isUp()) {
                continue;
            }
            if (networkInterface.isVirtual()) {
                continue;
            }
            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                InetAddress inetAddress = interfaceAddress.getAddress();
                if (inetAddress instanceof Inet6Address) {
                    continue;
                }
                list.add(inetAddress);
            }
        }
        return new InetAddressCollection(list.build());
    }
}
