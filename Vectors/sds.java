package com.friendsofed.gameElements.primitives
{
import flash.events.Event;
import flash.events.EventDispatcher;

  //ABSTRACT CLASS - Do not instantiate
public class AVerletModel extends EventDispatcher
{
    //Properties that don't require validation
public var previousX:Number = 0;
public var previousY:Number = 0;
public var temporaryX:Number = 0;
public var temporaryY:Number = 0;
public var rotationSpeed:Number = 0;
public var acceleration_X:Number = 0;
public var acceleration_Y:Number = 0;
public var acceleration:Number = 0;
public var frictionConstant:Number = 0.96; //Global friction
public var friction:Number = frictionConstant;
public var friction_Vx:Number = 0;
public var friction_Vy:Number = 0;
public var width:uint = 1;
public var height:uint = 1;
public var gravity_Vx:Number = 0;
public var gravity_Vy:Number = 0;
public var color:uint = 0x999999;
//Properties that require validation
//by getters and setters
private var _xPos:Number = 0;
private var _yPos:Number = 0;
private var _angle:Number = 0;
private var _visible:Boolean = true;
private var _rotationValue:Number = 0;

public function AVerletModel():void 
{
}
public function update():void
{
temporaryX = xPos;
temporaryY = yPos;
vx += acceleration_X;
vy += acceleration_Y;
vx *= friction;
vy *= friction;
//Optional: speed trap
/*
if((Math.abs(vx) < 0.05) && (Math.abs(vy) < 0.05))
{
 _acceleration_X = 0;
 _acceleration_Y = 0;
}
*/
      
xPos += vx + friction_Vx + gravity_Vx;
yPos += vy + friction_Vy + gravity_Vy;
previousX = temporaryX;
previousY = temporaryY;
//Listen for optional "update" events if you need to
//syncrchonize any code with this object's frame updates
dispatchEvent(new Event("update"));
}
//angle
public function get angle():Number
{
return _angle;
}
public function set angle(value:Number):void
{
_angle = value;
dispatchEvent(new Event(Event.CHANGE));
}
//vx
public function get vx():Number
{
return _xPos - previousX;
}
public function set vx(value:Number):void
{
previousX = _xPos - value;
}
//vy
public function get vy():Number
{
return _yPos - previousY;
}
public function set vy(value:Number):void
{
previousY = _yPos - value;
}
//xPos
public function get xPos():Number
{
return _xPos;
}
public function set xPos(value:Number):void
{
_xPos = value;
dispatchEvent(new Event(Event.CHANGE));
}
//yPos
public function get yPos():Number
{
return _yPos;
}
public function set yPos(value:Number):void
{
_yPos = value;
dispatchEvent(new Event(Event.CHANGE));
}
//setX
public function set setX(value:Number):void
{
 previousX = value - vx;
xPos = value;
}
//setY
public function set setY(value:Number):void
{
previousY = value - vy;
yPos = value;
}
//visible
public function get visible():Boolean
{
return _visible;
}
public function set visible(value:Boolean):void
{
_visible = value;
dispatchEvent(new Event(Event.CHANGE));
}
//rotationValue
public function get rotationValue():Number
{
return _rotationValue;
}
public function set rotationValue(value:Number):void
{
_rotationValue = value;
if(_rotationValue > 360)
{
 _rotationValue = 0;
}
}

//Need separate moveTo method for the set methods and a separate move method for all
//the classes that extend this basic one 
    //moveTo would call move 
    
    
    //update method calls move 
    //set methods call moveTo 
}
}