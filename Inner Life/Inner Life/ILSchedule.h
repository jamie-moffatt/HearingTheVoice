//
//  ILSchedule.h
//  Inner Life
//
//  Created by Matthew Bates on 12/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ILSchedule : NSObject

@property NSMutableArray *sessions;

- (void)add:(NSArray *)session;

@end
