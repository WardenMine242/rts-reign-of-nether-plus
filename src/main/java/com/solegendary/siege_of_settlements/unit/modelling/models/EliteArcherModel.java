package com.solegendary.siege_of_settlements.unit.modelling.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.solegendary.siege_of_settlements.SiegeOfSettlements;
import com.WardenMine242.siege_of_settlements.unit.units.villagers.EliteArcherUnit;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EliteArcherModel<T extends EliteArcherUnit> extends EntityModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(SiegeOfSettlements.MOD_ID, "elite_archer_layer"), "main");

    private final ModelPart head;
    private final ModelPart nose;
    private final ModelPart body;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart arrow;
    private final ModelPart bow;
    private final ModelPart bowstring1;
    private final ModelPart bowstring2;
    private final ModelPart arrow2;

    public EliteArcherModel(ModelPart root) {
        this.head = root.getChild("head");
        this.nose = this.head.getChild("nose");
        this.body = root.getChild("body");
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
        this.arrow = root.getChild("arrow");
        this.bow = this.rightArm.getChild("bow");
        this.bowstring1 = this.bow.getChild("bowstring");
        this.bowstring2 = this.bow.getChild("bowstring2");
        this.arrow2 = root.getChild("arrow2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        root.getChild("head").addOrReplaceChild("nose",
                CubeListBuilder.create().texOffs(24, 0)
                        .addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(16, 20)
                        .addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        body.addOrReplaceChild("body_overlay",
                CubeListBuilder.create().texOffs(52, 0)
                        .addBox(-3.0F, -2.0F, -1.0F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(3.0F, 3.0F, 4.0F, 0.0F, 0.0F, -0.5236F));

        root.addOrReplaceChild("left_arm",
                CubeListBuilder.create().texOffs(40, 46)
                        .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(-5.0F, 2.0F, 0.0F));

        root.addOrReplaceChild("right_arm",
                CubeListBuilder.create().texOffs(40, 46).mirror()
                        .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(5.0F, 2.0F, 0.0F));

        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(0, 22).mirror()
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(-2.0F, 12.0F, 0.0F));

        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(0, 22)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(2.0F, 12.0F, 0.0F));

        PartDefinition bow = root.getChild("right_arm").addOrReplaceChild("bow",
                CubeListBuilder.create()
                        .texOffs(0, 42).addBox(-1.0F, -12.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 56).addBox(-8.0F, -17.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(8, 56).addBox(-3.0F, -12.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(1.0F, 4.0F, 13.0F, -1.5708F, -0.7854F, -1.5708F));

        bow.addOrReplaceChild("bowstring",
                CubeListBuilder.create().texOffs(0, 63)
                        .addBox(-13.0F, -1.0F, 1.0F, 12.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.0F, 0.0F, -0.7854F));

        bow.addOrReplaceChild("bowstring2",
                CubeListBuilder.create().texOffs(24, 63)
                        .addBox(-13.0F, -1.0F, 1.0F, 12.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-8.0F, -8.0F, -1.0F, 0.0F, 0.0F, -0.7854F));

        bow.addOrReplaceChild("bow_limb_rot1",
                CubeListBuilder.create().texOffs(8, 42)
                        .addBox(-1.0F, -12.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-18.0F, -18.0F, 0.0F, 0.0F, 0.0F, -1.5708F));

        bow.addOrReplaceChild("bow_limb_rot2",
                CubeListBuilder.create().texOffs(0, 38)
                        .addBox(-5.0F, -2.0F, -2.1F, 6.0F, 2.0F, 2.2F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-4.0F, -11.0F, 1.0F, 0.0F, 0.0F, -0.7854F));

        root.addOrReplaceChild("arrow",
                CubeListBuilder.create().texOffs(54, 16)
                        .addBox(-4.0F, -16.0F, -0.5F, 5.0F, 16.0F, 0.0F, new CubeDeformation(0.0F))
                        .texOffs(54, 16)
                        .addBox(-2.0F, -16.0F, -3.0F, 5.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(4.0F, -5.0F, 5.0F, 0.0F, 0.0F, 2.618F));

        root.addOrReplaceChild("arrow2", CubeListBuilder.create().texOffs(54, 32)
                        .addBox(-4.0F, -16.0F, -0.5F, 5.0F, 16.0F, 0.0F, new CubeDeformation(0.0F))
                        .texOffs(54, 32)
                        .addBox(-2.0F, -16.0F, -3.0F, 5.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-2.0F, 1.0F, -21.0F, 1.5708F, 0.0F, -3.1416F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        resetPoses();

        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);

        float walkSpeed = 0.6662F;
        this.rightLeg.xRot = Mth.cos(limbSwing * walkSpeed) * 1.4F * limbSwingAmount * 0.5F;
        this.leftLeg.xRot = Mth.cos(limbSwing * walkSpeed + (float) Math.PI) * 1.4F * limbSwingAmount * 0.5F;

        if (!entity.isUsingItem()) {
            this.rightArm.xRot = Mth.cos(limbSwing * walkSpeed + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
            this.leftArm.xRot = Mth.cos(limbSwing * walkSpeed) * 2.0F * limbSwingAmount * 0.5F;
        }

        float breathe = Mth.sin(ageInTicks * 0.09F) * 0.03F;
        this.body.xRot += breathe;
        this.head.y += Mth.sin(ageInTicks * 0.09F) * 0.35F;

        // Keep bow in idle hand position by default.
        this.bow.xRot += 0.06F;
        this.bow.yRot += 0.02F;

        // Hidden unless aiming.
        this.arrow2.visible = false;

        applyShootAnimation(entity);
    }

    private void applyShootAnimation(T entity) {
        if (!entity.isUsingItem()) {
            return;
        }

        ItemStack activeItem = entity.getUseItem();
        float chargeDuration = 25.0F;
        if (!activeItem.isEmpty() && activeItem.getItem() instanceof CrossbowItem) {
            chargeDuration = CrossbowItem.getChargeDuration(activeItem);
        }
        float pull = Mth.clamp(entity.getTicksUsingItem() / Math.max(1.0F, chargeDuration), 0.0F, 1.0F);

        // Raise bow.
        this.rightArm.yRot = -0.1F + this.head.yRot;
        this.rightArm.xRot = (-(float) Math.PI / 2F) + this.head.xRot;
        this.leftArm.xRot = -0.9424779F + this.head.xRot;
        this.leftArm.yRot = this.head.yRot - 0.4F;
        this.leftArm.zRot = ((float) Math.PI / 2F);

        this.bow.xRot += -0.22F * pull;
        this.bow.yRot += -0.12F * pull;

        // Show and draw loaded arrow with the string.
        this.arrow2.visible = true;
        this.arrow2.x = -2.0F - (pull * 1.8F);
        this.arrow2.z = -21.0F + (pull * 0.2F);

        this.bowstring1.x = -(pull * 1.8F);
        this.bowstring2.x = -(pull * 1.8F);
    }

    private void resetPoses() {
        this.head.resetPose();
        this.nose.resetPose();
        this.body.resetPose();
        this.leftArm.resetPose();
        this.rightArm.resetPose();
        this.leftLeg.resetPose();
        this.rightLeg.resetPose();
        this.arrow.resetPose();
        this.bow.resetPose();
        this.bowstring1.resetPose();
        this.bowstring2.resetPose();
        this.arrow2.resetPose();
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.arrow.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.arrow2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
