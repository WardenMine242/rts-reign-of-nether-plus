package com.solegendary.siege_of_settlements.unit.modelling.models;

import com.WardenMine242.siege_of_settlements.unit.units.villagers.EliteArmoredVindicatorUnit;
import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class EliteArmoredVindicatorModel<T extends EliteArmoredVindicatorUnit> extends VillagerUnitModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "elite_armored_vindicator_layer"), "main");

    public EliteArmoredVindicatorModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition root = meshDefinition.getRoot();

        PartDefinition head = root.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(32, 0)
                        .addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.5F)),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );

        // Required by HumanoidModel constructor (root child "hat").
        root.addOrReplaceChild(
                "hat",
                CubeListBuilder.create()
                        .texOffs(32, 0)
                        .addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.5F)),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );

        PartDefinition hat = head.addOrReplaceChild(
                "hat",
                CubeListBuilder.create(),
                PartPose.ZERO
        );

        hat.addOrReplaceChild(
                "hat_rim",
                CubeListBuilder.create(),
                PartPose.ZERO
        );

        head.addOrReplaceChild(
                "nose",
                CubeListBuilder.create()
                        .texOffs(24, 0)
                        .addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -2.0F, 0.0F)
        );

        head.addOrReplaceChild(
                "horns",
                CubeListBuilder.create()
                        .texOffs(56, 18)
                        .addBox(-1.5F, -3.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(56, 18)
                        .addBox(9.5F, -3.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-5.0F, -9.0F, 0.0F, 0.6109F, 0.0F, 0.0F)
        );

        root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(16, 20)
                        .addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );

        root.getChild("body").addOrReplaceChild(
                "jacket",
                CubeListBuilder.create()
                        .texOffs(0, 38)
                        .addBox(-4.0F, 0.0F, -3.0F, 8.0F, 19.0F, 6.0F, new CubeDeformation(0.5F)),
                PartPose.ZERO
        );

        PartDefinition arms = root.addOrReplaceChild(
                "arms",
                CubeListBuilder.create()
                        .texOffs(44, 22)
                        .addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F)
                        .texOffs(40, 38)
                        .addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 3.0F, -1.0F, -0.75F, 0.0F, 0.0F)
        );

        arms.addOrReplaceChild(
                "left_shoulder",
                CubeListBuilder.create()
                        .texOffs(44, 22)
                        .mirror()
                        .addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F)
                        .mirror(false),
                PartPose.ZERO
        );

        root.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create()
                        .texOffs(0, 22)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(-2.0F, 12.0F, 0.0F)
        );

        root.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create()
                        .texOffs(0, 22)
                        .mirror()
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F)
                        .mirror(false),
                PartPose.offset(2.0F, 12.0F, 0.0F)
        );

        root.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create()
                        .texOffs(40, 46)
                        .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(-5.0F, 2.0F, 0.0F)
        );

        root.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create()
                        .texOffs(40, 46)
                        .mirror()
                        .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F)
                        .mirror(false),
                PartPose.offset(5.0F, 2.0F, 0.0F)
        );

        return LayerDefinition.create(meshDefinition, 64, 64);
    }
}
