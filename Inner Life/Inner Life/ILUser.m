//
//  ILUser.m
//  Inner Life
//
//  Created by Matthew Bates on 28/07/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILUser.h"

@implementation ILUser


- (id)initWithCoder:(NSCoder *)decoder
{
    self = [super init];
    self->_userID = [decoder decodeIntegerForKey:@"userID"];
    self->_userCode = [decoder decodeObjectForKey:@"userCode"];
    self->_age = [decoder decodeIntegerForKey:@"userAge"];
    self->_gender = [decoder decodeObjectForKey:@"userGender"];
    return self;
}

- (void)encodeWithCoder:(NSCoder *)coder
{
    [coder encodeInteger:self->_userID forKey:@"userID"];
    [coder encodeObject:self->_userCode forKey:@"userCode"];
    [coder encodeInteger:self->_age forKey:@"userAge"];
    [coder encodeObject:self->_gender forKey:@"userGender"];
}

-(NSString *)description
{
    return [NSString stringWithFormat:@"(%d, \"%@\", %d, %@)", _userID, _userCode, _age, _gender];
}

@end
