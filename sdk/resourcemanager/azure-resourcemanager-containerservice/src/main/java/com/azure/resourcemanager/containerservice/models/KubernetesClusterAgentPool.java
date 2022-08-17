// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.resourcemanager.containerservice.models;

import com.azure.core.annotation.Fluent;
import com.azure.resourcemanager.resources.fluentcore.arm.models.ChildResource;
import com.azure.resourcemanager.resources.fluentcore.model.Accepted;
import com.azure.resourcemanager.resources.fluentcore.model.Attachable;
import com.azure.resourcemanager.resources.fluentcore.model.HasInnerModel;
import com.azure.resourcemanager.resources.fluentcore.model.Settable;

import java.util.List;
import java.util.Map;

/** A client-side representation for a Kubernetes cluster agent pool. */
@Fluent
public interface KubernetesClusterAgentPool
    extends ChildResource<KubernetesCluster>, HasInnerModel<ManagedClusterAgentPoolProfile> {

    /** @return the provisioning state of the agent pool */
    String provisioningState();

    /** @return the number of agents (virtual machines) to host docker containers */
    int count();

    /** @return size of each agent virtual machine in the agent pool */
    ContainerServiceVMSizeTypes vmSize();

    /** @return OS disk size in GB set for each virtual machine in the agent pool */
    int osDiskSizeInGB();

    /** @return OS of each virtual machine in the agent pool */
    OSType osType();

    /** @return agent pool type */
    AgentPoolType type();

    /** @return agent pool mode */
    AgentPoolMode mode();

    /** @return the name of the subnet used by each virtual machine in the agent pool */
    String subnetName();

    /** @return the ID of the virtual network used by each virtual machine in the agent pool */
    String networkId();

    /** @return the list of availability zones */
    List<String> availabilityZones();

    /** @return the map of node labels */
    Map<String, String> nodeLabels();

    /** @return the list of node taints */
    List<String> nodeTaints();

    /** @return the power state, Running or Stopped */
    PowerState powerState();

    /** @return the number of agents (VMs) to host docker containers */
    int nodeSize();

    /** @return the maximum number of pods per node */
    int maximumPodsPerNode();

    /** @return whether auto-scaling is enabled */
    boolean isAutoScalingEnabled();

    /** @return the minimum number of nodes for auto-scaling */
    int minimumNodeSize();

    /** @return the maximum number of nodes for auto-scaling */
    int maximumNodeSize();

    /** @return the priority of each virtual machines in the agent pool */
    ScaleSetPriority virtualMachinePriority();

    /** @return the eviction policy of each virtual machines in the agent pool */
    ScaleSetEvictionPolicy virtualMachineEvictionPolicy();

    /** @return the maximum price of each spot virtual machines in the agent pool, -1 means pay-as-you-go prices */
    Double virtualMachineMaximumPrice();

    /**
     * @return the OS disk type to be used for machines in the agent pool
     */
    OSDiskType osDiskType();

    /**
     * @return the disk type for the placement of emptyDir volumes, container runtime data root,
     * and Kubelet ephemeral storage
     */
    KubeletDiskType kubeletDiskType();

    /**
     * @return the tags of the agents.
     */
    Map<String, String> tags();

//    /**
//     * Starts the agent pool.
//     */
//    void start();
//
//    /**
//     * Starts the agent pool.
//     *
//     * @return A {@link Mono} that completes when a successful response is received.
//     */
//    Mono<Void> startAsync();
//
//    /**
//     * Stops the agent pool.
//     */
//    void stop();
//
//    /**
//     * Stops the agent pool.
//     *
//     * @return A {@link Mono} that completes when a successful response is received.
//     */
//    Mono<Void>  stopAsync();

    // Fluent interfaces

    /**
     * The entirety of a container service agent pool definition as a part of a parent definition.
     *
     * @param <ParentT> the stage of the container service definition to return to after attaching this definition
     */
    interface Definition<ParentT, T>
        extends DefinitionStages.Blank<T>,
            DefinitionStages.WithOSType<T>,
            DefinitionStages.WithOSDiskSize<T>,
            DefinitionStages.WithAgentPoolType<T>,
            DefinitionStages.WithAgentPoolVirtualMachineCount<T>,
            DefinitionStages.WithMaxPodsCount<T>,
            DefinitionStages.WithVirtualNetwork<T>,
            DefinitionStages.WithAttach<ParentT, T>,
            DefinitionStages.WithBeginCreate<T> {
    }

    /** Grouping of container service agent pool definition stages as a part of parent container service definition. */
    interface DefinitionStages {

        /**
         * The first stage of a container service agent pool definition allowing to specify the agent virtual machine
         * size.
         *
         * @param <T> the stage of the container service definition to return to after attaching this definition
         */
        interface Blank<T> {
            /**
             * Specifies the size of the virtual machines to be used as agents.
             *
             * @param vmSize the size of each virtual machine in the agent pool
             * @return the next stage of the definition
             */
            WithAgentPoolVirtualMachineCount<T> withVirtualMachineSize(ContainerServiceVMSizeTypes vmSize);
        }

        /**
         * The stage of a container service agent pool definition allowing to specify the agent pool OS type.
         *
         * @param <T> the stage of the container service definition to return to after attaching this definition
         */
        interface WithOSType<T> {
            /**
             * OS type to be used for each virtual machine in the agent pool.
             *
             * <p>Default is Linux.
             *
             * @param osType OS type to be used for each virtual machine in the agent pool
             * @return the next stage of the definition
             */
            T withOSType(OSType osType);
        }

        /**
         * The stage of a container service agent pool definition allowing to specify the agent pool OS disk size.
         *
         * @param <T> the stage of the container service definition to return to after attaching this definition
         */
        interface WithOSDiskSize<T> {
            /**
             * OS disk size in GB to be used for each virtual machine in the agent pool.
             *
             * @param osDiskSizeInGB OS Disk Size in GB to be used for every machine in the agent pool
             * @return the next stage of the definition
             */
            T withOSDiskSizeInGB(int osDiskSizeInGB);
        }

        /**
         * The stage of a container service agent pool definition allowing to specify the type of agent pool. Allowed
         * values could be seen in AgentPoolType Class.
         *
         * @param <T> the stage of the container service definition to return to after attaching this definition
         */
        interface WithAgentPoolType<T> {
            /**
             * Set agent pool type to every virtual machine in the agent pool.
             *
             * @param agentPoolType the agent pool type for every machine in the agent pool
             * @return the next stage of the definition
             */
            T withAgentPoolType(AgentPoolType agentPoolType);

            /**
             * Set agent pool type by type name.
             *
             * @param agentPoolTypeName the agent pool type name in string format
             * @return the next stage of the definition
             */
            T withAgentPoolTypeName(String agentPoolTypeName);
        }

        /**
         * The stage of a container service agent pool definition allowing to specify the number of agents (Virtual
         * Machines) to host docker containers.
         *
         * <p>Allowed values must be in the range of 1 to 100 (inclusive); the default value is 1.
         *
         * @param <T> the stage of the container service definition to return to after attaching this definition
         */
        interface WithAgentPoolVirtualMachineCount<T> {
            /**
             * Specifies the number of agents (Virtual Machines) to host docker containers.
             *
             * @param count the number of agents (VMs) to host docker containers. Allowed values must be in the range of
             *     1 to 100 (inclusive); the default value is 1.
             * @return the next stage of the definition
             */
            T withAgentPoolVirtualMachineCount(int count);
        }

        /**
         * The stage of a container service agent pool definition allowing to specify the maximum number of pods that
         * can run on a node.
         *
         * @param <T> the stage of the container service definition to return to after attaching this definition
         */
        interface WithMaxPodsCount<T> {
            /**
             * Specifies the maximum number of pods that can run on a node.
             *
             * @param podsCount the maximum number of pods that can run on a node
             * @return the next stage of the definition
             */
            T withMaxPodsCount(int podsCount);
        }

        /**
         * The stage of a container service agent pool definition allowing to specify auto-scaling.
         *
         * @param <T> the stage of the container service definition to return to after attaching this definition
         */
        interface WithAutoScaling<T> {
            /**
             * Enables the auto-scaling with maximum/minimum number of nodes.
             *
             * @param minimumNodeSize the minimum number of nodes for auto-scaling.
             * @param maximumNodeSize the maximum number of nodes for auto-scaling.
             * @return the next stage of the definition
             */
            T withAutoScaling(int minimumNodeSize, int maximumNodeSize);
        }

        /**
         * The stage of a container service agent pool definition allowing to specify availability zones.
         *
         * @param <T> the stage of the container service definition to return to after attaching this definition
         */
        interface WithAvailabilityZones<T> {
            /**
             * Specifies the availability zones.
             *
             * @param zones the availability zones, can be 1, 2, 3.
             * @return the next stage of the definition
             */
            T withAvailabilityZones(Integer... zones);
        }

        /**
         * The stage of a container service agent pool definition allowing to specify node labels and taints.
         *
         * @param <T> the stage of the container service definition to return to after attaching this definition
         */
        interface WithNodeLabelsTaints<T> {
            /**
             * Specifies the node labels for all nodes.
             *
             * @param nodeLabels the node labels.
             * @return the next stage of the definition
             */
            T withNodeLabels(Map<String, String> nodeLabels);

            /**
             * Specifies the node labels.
             *
             * @param nodeTaints the node taints for new nodes.
             * @return the next stage of the definition
             */
            T withNodeTaints(List<String> nodeTaints);
        }

        /**
         * The stage of a container service agent pool definition allowing to specify tags.
         *
         * @param <T> the stage of the container service definition to return to after attaching this definition
         */
        interface WithTags<T> {
            /**
             * Specifies tags for the agents.
             *
             * @param tags the tags to associate
             * @return the next stage of the definition
             */
            T withTags(Map<String, String> tags);

            /**
             * Adds a tag to the agents.
             *
             * @param key the key for the tag
             * @param value the value for the tag
             * @return the next stage of the definition
             */
            T withTag(String key, String value);
        }

        /**
         * The stage of a container service agent pool definition allowing to specify a virtual network to be used for
         * the agents.
         *
         * @param <T> the stage of the container service definition to return to after attaching this definition
         */
        interface WithVirtualNetwork<T> {
            /**
             * Specifies the virtual network to be used for the agents.
             *
             * @param virtualNetworkId the ID of a virtual network
             * @param subnetName the name of the subnet within the virtual network.; the subnet must have the service
             *     endpoints enabled for 'Microsoft.ContainerService'.
             * @return the next stage
             */
            T withVirtualNetwork(String virtualNetworkId, String subnetName);
        }

        /**
         * The stage of a container service agent pool definition allowing to specify the agent pool mode.
         *
         * @param <T> the stage of the container service definition to return to after attaching this definition
         */
        interface WithAgentPoolMode<T> {
            /**
             * Specifies the agent pool mode for the agents.
             *
             * @param agentPoolMode the agent pool mode
             * @return the next stage of the definition
             */
            T withAgentPoolMode(AgentPoolMode agentPoolMode);
        }

        /**
         * The stage of a container service agent pool definition allowing to specify the priority of the virtual
         * machine.
         *
         * @param <T> the stage of the container service definition to return to after attaching this definition
         */
        interface WithVMPriority<T> {
            /**
             * Specifies the priority of the virtual machines.
             *
             * @param priority the priority
             * @return the next stage of the definition
             */
            T withVirtualMachinePriority(ScaleSetPriority priority);

            /**
             * Specify that virtual machines should be spot priority VMs.
             *
             * @return the next stage of the definition
             */
            T withSpotPriorityVirtualMachine();

            /**
             * Specify that virtual machines should be spot priority VMs with provided eviction policy.
             *
             * @param policy eviction policy for the virtual machines.
             * @return the next stage of the definition
             */
            T withSpotPriorityVirtualMachine(ScaleSetEvictionPolicy policy);
        }

        /**
         * The stage of a container service agent pool definition allowing to specify the agent pool mode.
         *
         * @param <T> the stage of the container service definition to return to after attaching this definition
         */
        interface WithBillingProfile<T> {
            /**
             * Sets the maximum price for virtual machine in agent pool. This price is in US Dollars.
             *
             * Default is -1 if not specified, as up to pay-as-you-go prices.
             *
             * @param maxPriceInUsDollars the maximum price in US Dollars
             * @return the next stage of the definition
             */
            T withVirtualMachineMaximumPrice(Double maxPriceInUsDollars);
        }

        /**
         * The stage of a container service agent pool definition allowing to specify the agent pool disk type.
         *
         * @param <T> the stage of the container service definition to return to after attaching this definition
         */
        interface WithDiskType<T> {
            /**
             * The OS disk type to be used for machines in the agent pool.
             *
             * The default is 'Ephemeral' if the VM supports it and has a cache disk larger than the requested
             * OSDiskSizeGB. Otherwise, defaults to 'Managed'.
             *
             * @param osDiskType the OS disk type to be used for machines in the agent pool
             * @return the next stage of the definition
             */
            T withOSDiskType(OSDiskType osDiskType);

            /**
             * The disk type for the placement of emptyDir volumes, container runtime data root,
             * and Kubelet ephemeral storage.
             *
             * @param kubeletDiskType the disk type for the placement of emptyDir volumes, container runtime data root,
             *                        and Kubelet ephemeral storage.
             * @return the next stage of the definition
             */
            T withKubeletDiskType(KubeletDiskType kubeletDiskType);
        }

        interface WithAttach<ParentT, T> extends
            Final<T>,
            Attachable.InDefinition<ParentT> {
        }

        /**
         * The final stage of a container service agent pool definition. At this stage, any remaining optional settings
         * can be specified, or the container service agent pool can be attached to the parent container service
         * definition.
         *
         * @param <T> the stage of the container service definition to return to after attaching this definition
         */
        interface Final<T> extends
            WithOSType<T>,
            WithOSDiskSize<T>,
            WithAgentPoolType<T>,
            WithAgentPoolVirtualMachineCount<T>,
            WithMaxPodsCount<T>,
            WithVirtualNetwork<T>,
            WithAgentPoolMode<T>,
            WithAutoScaling<T>,
            WithAvailabilityZones<T>,
            WithNodeLabelsTaints<T>,
            WithVMPriority<T>,
            WithBillingProfile<T>,
            WithDiskType<T>,
            WithTags<T> {
        }

        interface WithBeginCreate<T> extends Final<T> {
            Accepted<KubernetesClusterAgentPool> beginCreate();
        }
    }

    /** The template for an update operation, containing all the settings that can be modified. */
    interface Update<ParentT, T>
        extends Settable<ParentT>,
            UpdateStages.WithAgentPoolVirtualMachineCount<T>,
            UpdateStages.WithAutoScaling<T>,
            UpdateStages.WithAgentPoolMode<T>,
            UpdateStages.WithDiskType<T>,
            UpdateStages.WithTags<T> {
    }

    /** Grouping of agent pool update stages. */
    interface UpdateStages {
        /**
         * The stage of a container service agent pool update allowing to specify the number of agents (Virtual
         * Machines) to host docker containers.
         *
         * <p>Allowed values must be in the range of 1 to 100 (inclusive); the default value is 1.
         *
         * @param <T> the stage of the container service definition to return to after attaching this definition
         */
        interface WithAgentPoolVirtualMachineCount<T> {
            /**
             * Specifies the number of agents (Virtual Machines) to host docker containers.
             *
             * @param count the number of agents (VMs) to host docker containers. Allowed values must be in the range of
             *     1 to 100 (inclusive); the default value is 1.
             * @return the next stage of the update
             */
            T withAgentPoolVirtualMachineCount(int count);
        }

        /**
         * The stage of a container service agent pool update allowing to specify the agent pool mode.
         *
         * @param <T> the stage of the container service definition to return to after attaching this definition
         */
        interface WithAgentPoolMode<T> {
            /**
             * Specifies the agent pool mode for the agents.
             *
             * @param agentPoolMode the agent pool mode
             * @return the next stage of the update
             */
            T withAgentPoolMode(AgentPoolMode agentPoolMode);
        }

        /**
         * The stage of a container service agent pool update allowing to specify tags.
         *
         * @param <T> the stage of the container service definition to return to after attaching this definition
         */
        interface WithTags<T> {
            /**
             * Specifies tags for the agents.
             *
             * @param tags tags indexed by name
             * @return the next stage of the update
             */
            T withTags(Map<String, String> tags);

            /**
             * Adds a tag to the agents.
             *
             * @param key the key for the tag
             * @param value the value for the tag
             * @return the next stage of the update
             */
            T withTag(String key, String value);

            /**
             * Removes a tag from the agents.
             *
             * @param key the key of the tag to remove
             * @return the next stage of the update
             */
             T withoutTag(String key);
        }

        /**
         * The stage of a container service agent pool update allowing to specify auto-scaling.
         *
         * @param <T> the stage of the container service definition to return to after attaching this definition
         */
        interface WithAutoScaling<T> {
            /**
             * Enables the auto-scaling with maximum/minimum number of nodes.
             *
             * @param minimumNodeCount the minimum number of nodes for auto-scaling.
             * @param maximumNodeCount the maximum number of nodes for auto-scaling.
             * @return the next stage of the update
             */
            T withAutoScaling(int minimumNodeCount, int maximumNodeCount);

            /**
             * Disables the auto-scaling with maximum/minimum number of nodes.
             *
             * @return the next stage of the update
             */
            T withoutAutoScaling();
        }

        /**
         * The stage of a container service agent pool update allowing to specify the agent pool disk type.
         *
         * @param <T> the stage of the container service definition to return to after attaching this definition
         */
        interface WithDiskType<T> {
            /**
             * The disk type for the placement of emptyDir volumes, container runtime data root,
             * and Kubelet ephemeral storage.
             *
             * @param kubeletDiskType the disk type for the placement of emptyDir volumes, container runtime data root,
             *                        and Kubelet ephemeral storage.
             * @return the next stage of the update
             */
            T withKubeletDiskType(KubeletDiskType kubeletDiskType);
        }
    }
}
