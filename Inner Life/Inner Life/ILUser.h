//
//  ILUser.h
//  Inner Life
//
//  Created by Matthew Bates on 28/07/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ILUser : NSObject <NSCoding>

@property NSInteger userID;
@property NSString *userCode;
@property NSInteger age;
@property NSString *gender;

@end
