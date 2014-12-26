package pixlepix.auracascade.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.shader.TesselatorVertexState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import pixlepix.auracascade.block.tile.AuraTilePedestal;

/**
 * Created by pixlepix on 12/6/14.
 */
public class RenderPedestal extends TileEntitySpecialRenderer {

    public EntityItem entityItem;

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
        RenderBlocks.getInstance().blockAccess = tileEntity.getWorldObj();
        RenderBlocks.getInstance().renderBlockAllFaces(tileEntity.blockType, (int)x, (int)y, (int)z);


        AuraTilePedestal pedestal = (AuraTilePedestal) tileEntity;
        pedestal.frames++;
        if(pedestal.itemStack != null) {
            if ((entityItem == null) || entityItem.getEntityItem().getItem() != pedestal.itemStack.getItem())
                entityItem = new EntityItem(tileEntity.getWorldObj(), x, y, z, ((AuraTilePedestal) tileEntity).itemStack);
            x = x + .5;
            y = y + 1.16;
            z = z + .5;

            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_LIGHTING);

            //This parameter is never used ._.
            Minecraft.getMinecraft().entityRenderer.disableLightmap(0D);

            float angle = (float) (pedestal.frames / 2D);

            entityItem.setRotationYawHead(angle);

            //Prevent 'jump' in the bobbing
            //Bobbing is calculated as the age plus the yaw
            entityItem.age = (int) (400F - entityItem.rotationYaw);

            RenderManager.instance.renderEntityWithPosYaw(this.entityItem, x, y, z, 0, angle);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();


            Minecraft.getMinecraft().entityRenderer.enableLightmap(0D);

            /*
            GL11.glPushMatrix();
            this.entityItem.hoverStart = 0.0F;
            RenderItem.renderInFrame = true;
            GL11.glTranslatef((float) x + 0.5F, (float) y + 2.02F, (float) z + 0.3F);
            //GL11.glRotatef(180, 0, 1, 1);

            RenderManager.instance.renderEntityWithPosYaw(this.entityItem, 0.0D, 0.0D, 0.0D, (float) Math.PI, 0);
            RenderItem.renderInFrame = false;
            GL11.glPopMatrix();
            */
        }
    }
}
