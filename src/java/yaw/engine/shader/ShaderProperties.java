package yaw.engine.shader;

import java.util.Objects;

public class ShaderProperties {
    // scene-specific properties
    private final boolean hasDirectionalLight;
    private final int maxPointLights;
    private final int maxSpotLights;

    // mesh-specific properties
    private final boolean hasTexture;

    private final boolean wishShadows;

    public ShaderProperties(boolean hasDirectionalLight, int maxPointLights, int maxSpotLights, boolean hasTexture, boolean wishShadows) {
        this.hasDirectionalLight = hasDirectionalLight;
        this.maxPointLights = maxPointLights;
        this.maxSpotLights = maxSpotLights;
        this.hasTexture = hasTexture;
        this.wishShadows = wishShadows;
    }

    public boolean hasDirectionalLight() {
        return hasDirectionalLight;
    }

    public int getMaxPointLights() {
        return maxPointLights;
    }

    public int getMaxSpotLights() {
        return maxSpotLights;
    }

    public boolean hasTexture() {
        return hasTexture;
    }

    public boolean withShadows() {
        return wishShadows;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShaderProperties that = (ShaderProperties) o;
        return hasDirectionalLight == that.hasDirectionalLight && maxPointLights == that.maxPointLights && maxSpotLights == that.maxSpotLights && hasTexture == that.hasTexture && wishShadows == that.wishShadows;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hasDirectionalLight, maxPointLights, maxSpotLights, hasTexture, wishShadows);
    }
}
