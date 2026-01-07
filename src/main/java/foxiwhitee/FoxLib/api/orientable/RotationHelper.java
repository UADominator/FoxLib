package foxiwhitee.FoxLib.api.orientable;

import net.minecraftforge.common.util.ForgeDirection;

public class RotationHelper {
    public static int getUVRotation(int side, ForgeDirection forward, ForgeDirection up) {
        if (forward == ForgeDirection.UNKNOWN) return 0;

        if (side == 0 || side == 1) {
            if (forward.offsetY == 0) {
                return switch (forward) {
                    case NORTH -> 3;
                    case WEST -> side == 0 ? 2 : 1;
                    case EAST -> side == 0 ? 1 : 2;
                    default -> 0;
                };
            } else {
                return switch (up) {
                    case NORTH -> 0;
                    case SOUTH -> 3;
                    case WEST -> side == 0 ? 2 : 1;
                    case EAST -> side == 0 ? 1 : 2;
                    default -> 0;
                };
            }
        }

        if (side > 1 && (forward == ForgeDirection.UP || forward == ForgeDirection.DOWN)) {
            return getSideRotation(side, forward, up);
        }

        return 0;
    }

    private static int getSideRotation(int side, ForgeDirection forward, ForgeDirection up) {
        ForgeDirection sideDir = ForgeDirection.getOrientation(side);


        if (sideDir == up.getOpposite()) {
            return (forward == ForgeDirection.UP) ? 1 : 1;
        }

        if (sideDir == up) {
            return (forward == ForgeDirection.UP) ? 2 : 2;
        }

        if (forward == ForgeDirection.UP) {
            return 3;
        } else {
            return 0;
        }
    }

    public static ForgeDirection rotateAround(final ForgeDirection forward, final ForgeDirection axis) {
        if (axis == ForgeDirection.UNKNOWN || forward == ForgeDirection.UNKNOWN) {
            return forward;
        }

        switch (forward) {
            case DOWN:
                switch (axis) {
                    case DOWN, UP -> {
                        return forward;
                    }
                    case NORTH -> {
                        return ForgeDirection.EAST;
                    }
                    case SOUTH -> {
                        return ForgeDirection.WEST;
                    }
                    case EAST -> {
                        return ForgeDirection.NORTH;
                    }
                    case WEST -> {
                        return ForgeDirection.SOUTH;
                    }
                    default -> {}
                }
                break;
            case UP:
                switch (axis) {
                    case NORTH -> {
                        return ForgeDirection.WEST;
                    }
                    case SOUTH -> {
                        return ForgeDirection.EAST;
                    }
                    case EAST -> {
                        return ForgeDirection.SOUTH;
                    }
                    case WEST -> {
                        return ForgeDirection.NORTH;
                    }
                    default -> {}
                }
                break;
            case NORTH:
                switch (axis) {
                    case UP -> {
                        return ForgeDirection.WEST;
                    }
                    case DOWN -> {
                        return ForgeDirection.EAST;
                    }
                    case EAST -> {
                        return ForgeDirection.UP;
                    }
                    case WEST -> {
                        return ForgeDirection.DOWN;
                    }
                    default -> {}
                }
                break;
            case SOUTH:
                switch (axis) {
                    case UP -> {
                        return ForgeDirection.EAST;
                    }
                    case DOWN -> {
                        return ForgeDirection.WEST;
                    }
                    case EAST -> {
                        return ForgeDirection.DOWN;
                    }
                    case WEST -> {
                        return ForgeDirection.UP;
                    }
                    default -> {}
                }
                break;
            case EAST:
                switch (axis) {
                    case UP -> {
                        return ForgeDirection.NORTH;
                    }
                    case DOWN -> {
                        return ForgeDirection.SOUTH;
                    }
                    case NORTH -> {
                        return ForgeDirection.UP;
                    }
                    case SOUTH -> {
                        return ForgeDirection.DOWN;
                    }
                    default -> {}
                }
            case WEST:
                switch (axis) {
                    case UP -> {
                        return ForgeDirection.SOUTH;
                    }
                    case DOWN -> {
                        return ForgeDirection.NORTH;
                    }
                    case NORTH -> {
                        return ForgeDirection.DOWN;
                    }
                    case SOUTH -> {
                        return ForgeDirection.UP;
                    }
                    default -> {}
                }
            default:
                break;
        }
        return forward;
    }
}
