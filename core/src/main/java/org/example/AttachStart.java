package org.example;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.IOException;

public class AttachStart {

    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        String pid = args[0];
        String agentPath = args[1];
        System.out.println("start attach process " + pid);
        System.out.println("agentPath:" + agentPath);
        VirtualMachine virtualMachine = VirtualMachine.attach(pid);
        try {
            virtualMachine.loadAgent(agentPath);
        } finally {
            virtualMachine.detach();
        }
    }
}
