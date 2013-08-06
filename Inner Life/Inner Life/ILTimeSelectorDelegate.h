//
//  ILTimeSelectorDelegate.h
//  Inner Life
//
//  Created by Matthew Bates on 06/08/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol ILTimeSelectorDelegate <NSObject>

- (void)timeWasChanged :(NSInteger)amStartTime :(NSInteger)pmStartTime;

@end
