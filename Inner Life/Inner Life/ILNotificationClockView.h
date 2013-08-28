//
//  ILNotificationClockView.h
//  Inner Life
//
//  Created by Matthew Bates on 18/08/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ILNotificationClockView : UIView
{
    @private
    UIImage* clockHand;
}

@property (nonatomic) NSInteger amStart;
@property (nonatomic) NSInteger amEnd;
@property (nonatomic) NSInteger pmStart;
@property (nonatomic) NSInteger pmEnd;
@property (nonatomic) NSInteger time;

@end
