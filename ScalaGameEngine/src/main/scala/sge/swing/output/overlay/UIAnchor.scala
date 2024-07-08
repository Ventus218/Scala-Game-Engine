package sge.swing.output.overlay

/** The anchor position used to compute the true screen position of a UI
  * element. It represents the starting point on the screen for calculating the
  * relative UI element position.
  */
enum UIAnchor:
  case TopLeft
  case TopCenter
  case TopRight
  case CenterLeft
  case Center
  case CenterRight
  case BottomLeft
  case BottomCenter
  case BottomRight
