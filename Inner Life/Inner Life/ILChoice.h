//
//  ILChoice.h
//  Inner Life
//
//  Created by Matthew Bates on 13/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ILChoice : NSObject

@property NSInteger choiceID;
@property NSString *text;
@property NSString *value;

- (id)initWithText: (NSString*)text andValue: (NSString*)value;
+ (const ILChoice *)NA;

@end
