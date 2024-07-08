package sge.swing.input

import java.awt.event.{MouseEvent, KeyEvent}
import KeyEvent.*

/** The buttons that can generate an input.
  *
  * @param id
  *   The Java AWT enum int value that identifies the button
  */
enum InputButton(val id: Int):
  case N_0 extends InputButton(VK_0)
  case N_1 extends InputButton(VK_1)
  case N_2 extends InputButton(VK_2)
  case N_3 extends InputButton(VK_3)
  case N_4 extends InputButton(VK_4)
  case N_5 extends InputButton(VK_5)
  case N_6 extends InputButton(VK_6)
  case N_7 extends InputButton(VK_7)
  case N_8 extends InputButton(VK_8)
  case N_9 extends InputButton(VK_9)
  case Q extends InputButton(VK_Q)
  case W extends InputButton(VK_W)
  case E extends InputButton(VK_E)
  case R extends InputButton(VK_R)
  case T extends InputButton(VK_T)
  case Y extends InputButton(VK_Y)
  case U extends InputButton(VK_U)
  case I extends InputButton(VK_I)
  case O extends InputButton(VK_O)
  case P extends InputButton(VK_P)
  case A extends InputButton(VK_A)
  case S extends InputButton(VK_S)
  case D extends InputButton(VK_D)
  case F extends InputButton(VK_F)
  case G extends InputButton(VK_G)
  case H extends InputButton(VK_H)
  case J extends InputButton(VK_J)
  case K extends InputButton(VK_K)
  case L extends InputButton(VK_L)
  case Z extends InputButton(VK_Z)
  case X extends InputButton(VK_X)
  case C extends InputButton(VK_C)
  case V extends InputButton(VK_V)
  case B extends InputButton(VK_B)
  case N extends InputButton(VK_N)
  case M extends InputButton(VK_M)
  case Space extends InputButton(VK_SPACE)

  /** Usually the left mouse button
    */
  case MouseButton1 extends InputButton(MouseEvent.BUTTON1)

  /** Usually the scrollwheel button
    */
  case MouseButton2 extends InputButton(MouseEvent.BUTTON2)

  /** Usually the right mouse button
    */
  case MouseButton3 extends InputButton(MouseEvent.BUTTON3)
