//
//  ILTimeSelector.h
//  Inner Life
//
//  Created by Matthew Bates on 06/08/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ILTimeSelectorDelegate.h"

#define NO_COLLISION 0
#define AM_TRIANGLE_COLLISION 1
#define PM_TRIANGLE_COLLISION 2

@interface ILTimeSelector : UIView
{
	NSInteger amStartTime;
	NSInteger pmStartTime;
    
	// {a, b, c}
    CGPoint amBoundingTriangle[3];
    CGPoint pmBoundingTriangle[3];
    
    CGFloat cx;
    CGFloat cy;
    
    BOOL touching;
    CGPoint currentTouchPoint;
    CGFloat theta;
    
    NSInteger hit;
}

@property id<ILTimeSelectorDelegate> delegate;

@end
